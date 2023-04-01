package com.uplus.zip.domain.comment.application.impl

import com.uplus.zip.domain.comment.application.CommentService
import com.uplus.zip.domain.comment.dao.CommentInfo
import com.uplus.zip.domain.comment.dao.CommentLikesRepository
import com.uplus.zip.domain.comment.dao.CommentRepository
import com.uplus.zip.domain.comment.domain.Comment
import com.uplus.zip.domain.comment.domain.CommentLikes
import com.uplus.zip.domain.comment.dto.request.CommentCreateRequestDto
import com.uplus.zip.domain.comment.exception.CommentErrorCode
import com.uplus.zip.domain.review.dao.ReviewRepository
import com.uplus.zip.domain.review.domain.Review
import com.uplus.zip.domain.review.exception.ReviewErrorCode
import com.uplus.zip.global.common.dto.PaginationMeta
import com.uplus.zip.global.common.dto.PaginationResponseDto
import com.uplus.zip.global.error.CustomException
import com.uplus.zip.global.util.S3Uploader
import com.uplus.zip.global.util.SecurityUtil
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val commentLikesRepository: CommentLikesRepository,
    private val reviewRepository: ReviewRepository,
    private val uploader: S3Uploader,
) : CommentService {
    @Transactional
    override fun createComment(
        reviewId: Long,
        commentCreateRequestDto: CommentCreateRequestDto
    ) {
        val (content, image) = commentCreateRequestDto
        val member = SecurityUtil.getLoginMember()
        val review: Review = reviewRepository.findByIdOrNull(reviewId)
            ?: throw CustomException(ReviewErrorCode.REVIEW_NOT_FOUND)
        // S3에 이미지 업로드
        val imageOriginUrl = image?.let { uploader.uploadOrigin(image) }
        val imageThumbnailUrl = image?.let { uploader.uploadResizing(image) }
        commentRepository.save(
            Comment(
                content = content,
                imageThumbnailUrl = imageThumbnailUrl,
                imageOriginUrl = imageOriginUrl,
                review = review,
                member = member
            )
        )
    }

    @Transactional
    override fun deleteComment(commentId: Long) {
        val member = SecurityUtil.getLoginMember()
        val comment = commentRepository.findByIdOrNull(commentId)
            ?.let {
                // 댓글의 주인이 맞는지 확인, 댓글의 주인이 아니라면 401 UNAUTHORIZED ERROR
                if (it.member.memberId == member.memberId) it else throw CustomException(CommentErrorCode.COMMENT_CANNOT_BE_DELETED)
            }
        // 댓글이 존재하지 않으면 404 NOTFOUND ERROR
            ?: throw CustomException(CommentErrorCode.COMMENT_NOT_FOUND)
        commentRepository.delete(comment)
        // S3 이미지 삭제
        comment.imageOriginUrl?.let {
            uploader.deleteImage(comment.imageOriginUrl!!)
            uploader.deleteImage(comment.imageThumbnailUrl!!)
        }
    }

    @Transactional
    override fun likeComment(commentId: Long) {
        val member = SecurityUtil.getLoginMember()
        val comment = commentRepository.findByIdOrNull(commentId)
            ?: throw CustomException(CommentErrorCode.COMMENT_NOT_FOUND)
        // 이미 좋아요한 댓글이면 취소, 아니면 좋아요
        commentLikesRepository.findByMemberAndComment(member, comment)
            ?.let { commentLikesRepository.delete(it) }
            ?: commentLikesRepository.save(CommentLikes(comment, member))
    }

    override fun getComments(
        reviewId: Long,
        startId: Long,
        size: Int
    ): PaginationResponseDto<CommentInfo> {
        reviewRepository.findByIdOrNull(reviewId) ?: throw CustomException(ReviewErrorCode.REVIEW_NOT_FOUND)
        // 로그인한 사용자면 memberId를 받는다
        val memberId = SecurityUtil.isLoginMember()?.memberId
        val limit = PageRequest.ofSize(size + 1)
        val totalComments = commentRepository.findAllByJpql(reviewId, memberId, startId, limit)
        // 다음 페이지 댓글이 존재하면 다음페이지에서 반환하기 위해 삭제, 다음 페이지 댓글이 존재하지 않으면 hasNext = false
        val hasNext = totalComments.totalElements >= size + 1
        val total = min(totalComments.totalElements, size.toLong())
        val page = totalComments.content.take(size)
        return PaginationResponseDto(
            page = page,
            meta = PaginationMeta(hasNext, total)
        )
    }
}