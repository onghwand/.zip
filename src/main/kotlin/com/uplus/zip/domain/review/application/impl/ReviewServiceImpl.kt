package com.uplus.zip.domain.review.application.impl

import com.uplus.zip.domain.comment.dao.CommentRepository
import com.uplus.zip.domain.maps.dao.ApartmentRepository
import com.uplus.zip.domain.maps.domain.Apartment
import com.uplus.zip.domain.maps.exception.ApartmentErrorCode
import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.review.application.ReviewService
import com.uplus.zip.domain.review.dao.ReviewLikesRepository
import com.uplus.zip.domain.review.dao.ReviewRepository
import com.uplus.zip.domain.review.domain.Review
import com.uplus.zip.domain.review.domain.ReviewLikes
import com.uplus.zip.domain.review.dto.ReviewCreateRequestDto
import com.uplus.zip.domain.review.dto.ReviewImageResponseDto
import com.uplus.zip.domain.review.dto.ReviewInfoResponseDto
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

@Service
class ReviewServiceImpl(
    private val reviewRepository: ReviewRepository,
    private val reviewLikesRepository: ReviewLikesRepository,
    private val apartmentRepository: ApartmentRepository,
    private val commentRepository: CommentRepository,
    private val likesRepository: ReviewLikesRepository,
    private val uploader: S3Uploader
) : ReviewService {
    @Transactional
    override fun createReview(
        reviewCreateRequestDto: ReviewCreateRequestDto,
        apartmentId: Long
    ) {
        val member: Member = SecurityUtil.getLoginMember()
        val apartment: Apartment =
            apartmentRepository.findByIdOrNull(apartmentId)
                ?: throw CustomException(ApartmentErrorCode.APARTMENT_NOT_FOUND)
        val imageOriginUrl = reviewCreateRequestDto.image?.let { uploader.uploadOrigin(reviewCreateRequestDto.image) }
        val imageThumbnailUrl =
            reviewCreateRequestDto.image?.let { uploader.uploadResizing(reviewCreateRequestDto.image) }
        val review = reviewCreateRequestDto.toEntity(
            member = member,
            apartment = apartment,
            imageOriginUrl = imageOriginUrl,
            imageThumbnailUrl = imageThumbnailUrl
        )
        reviewRepository.save(review)
    }

    override fun getReview(reviewId: Long): ReviewInfoResponseDto? {
        val member: Member? = SecurityUtil.isLoginMember()
        val review =
            reviewRepository.findByIdOrNull(reviewId) ?: throw CustomException(ReviewErrorCode.REVIEW_NOT_FOUND)
        return makeReviewInfoResponseDto(member, review)
    }

    @Transactional(readOnly = true)
    override fun getReviews(
        apartmentId: Long,
        startId: Long,
        size: Int,
    ): PaginationResponseDto<ReviewInfoResponseDto> {
        val member: Member? = SecurityUtil.isLoginMember()
        val pageable = PageRequest.of(0, size + 1)
        val apartment = apartmentRepository.findByIdOrNull(apartmentId)
        val reviewPages = reviewRepository.findByApartmentAndReviewIdLessThanOrderByCreatedAtDesc(
            apartment = apartment
                ?: throw CustomException(ApartmentErrorCode.APARTMENT_NOT_FOUND),
            reviewId = startId,
            pageable = pageable
        )
        var responseList = mutableListOf<ReviewInfoResponseDto>()
        reviewPages.forEach { review ->
            responseList.add(makeReviewInfoResponseDto(member = member, review = review))
        }
        val totalCount = reviewRepository.countByApartment(apartment = apartment)
        val hasMore = responseList.size > size
        return PaginationResponseDto(responseList.take(size), PaginationMeta(hasMore, totalCount))
    }


    override fun deleteReview(reviewId: Long) {
        val imageList = deleteData(reviewId)
        uploader.deleteImage(imageList)
    }

    @Transactional
    fun deleteData(reviewId: Long): MutableList<String> {
        val member = SecurityUtil.getLoginMember()
        // 게시글 삭제 요청자와 게시글 생성자가 같은지 확인
        val review =
            reviewRepository.findByIdOrNull(reviewId) ?: throw CustomException(ReviewErrorCode.REVIEW_NOT_FOUND)
        if (review.member.memberId != member.memberId) {
            throw CustomException(ReviewErrorCode.REVIEW_FORBIDDEN)
        } else {
            val deleteImageList = review.comments.flatMap { comment ->
                comment.imageOriginUrl?.let { listOf(comment.imageOriginUrl!!, comment.imageThumbnailUrl!!) }
                    ?: emptyList()
            }.toMutableList()
            // 리뷰 이미지 추가
            review.imageOriginUrl?.let {
                deleteImageList.add(review.imageOriginUrl!!)
                deleteImageList.add(review.imageThumbnailUrl!!)
            }
            // 리뷰 삭제(댓글까지 삭제됨)
            reviewRepository.delete(review)
            return deleteImageList
        }
        return mutableListOf()
    }

    @Transactional
    override fun changeReviewLike(reviewId: Long) {
        val member = SecurityUtil.getLoginMember()
        val review =
            reviewRepository.findByIdOrNull(reviewId) ?: throw CustomException(ReviewErrorCode.REVIEW_NOT_FOUND)
        reviewLikesRepository.findByReviewAndMember(review, member)?.let {
            reviewLikesRepository.delete(it)
        } ?: reviewLikesRepository.save(ReviewLikes(review, member))
    }

    @Transactional(readOnly = true)
    override fun getReviewsAndImages(
        apartmentId: Long,
        startId: Long,
        size: Int
    ): PaginationResponseDto<ReviewImageResponseDto> {
        val apartment = apartmentRepository.findByIdOrNull(apartmentId)
            ?: throw CustomException(ApartmentErrorCode.APARTMENT_NOT_FOUND)
        val pageable = PageRequest.of(0, size + 1)
        val imagesDaoPage =
            reviewRepository.findByApartmentAndReviewIdLessThanAndImageOriginUrlNotNullOrderByCreatedAtDesc(
                apartment,
                startId,
                pageable
            )
        val hasMore = imagesDaoPage.totalElements > size
        val loginMember = SecurityUtil.isLoginMember()
        val totalCount = reviewRepository.countByApartmentAndImageOriginUrlNotNullOrderByCreatedAtDesc(apartment)
        val imagesResponse = mutableListOf<ReviewImageResponseDto>()
        imagesDaoPage.forEach { page ->
            imagesResponse.add(
                ReviewImageResponseDto(
                    id = page.reviewId,
                    imageOriginUrl = page.imageOriginUrl,
                    imageThumbnailUrl = page.imageThumbnailUrl,
                    content = page.content,
                    createdAt = page.createdAt,
                    nickname = page.member.nickname,
                    memberId = page.member.memberId,
                    reviewLikeCnt = page.reviewLikes.size,
                    commentCnt = page.comments.size,
                    likeStatus = if (loginMember in page.reviewLikes.map { reviewLikes -> reviewLikes.member }) 1 else 0
                )
            )
        }
        return PaginationResponseDto(imagesResponse.take(size), PaginationMeta(hasMore, totalCount))
    }

    @Transactional(readOnly = true)
    fun makeReviewInfoResponseDto(member: Member?, review: Review): ReviewInfoResponseDto {
        val countComment = commentRepository.countByReview(review)
        val countLike = likesRepository.countByReview(review)
        val likeStatus = if (member?.run { likesRepository.findByReviewAndMember(review, this) } != null) 1 else 0
        return ReviewInfoResponseDto(
            id = review.reviewId,
            nickname = review.member.nickname,
            memberId = review.member.memberId,
            createdAt = review.createdAt,
            content = review.content,
            imageOriginUrl = review.imageOriginUrl,
            imageThumbnailUrl = review.imageThumbnailUrl,
            commentCnt = countComment,
            likeStatus = likeStatus,
            reviewLikeCnt = countLike
        )
    }
}