package com.uplus.zip.domain.comment.application

import com.uplus.zip.domain.comment.application.impl.CommentServiceImpl
import com.uplus.zip.domain.comment.dao.CommentLikesRepository
import com.uplus.zip.domain.comment.dao.CommentRepository
import com.uplus.zip.domain.comment.domain.Comment
import com.uplus.zip.domain.comment.domain.CommentLikes
import com.uplus.zip.domain.comment.dto.request.CommentCreateRequestDto
import com.uplus.zip.domain.comment.dao.CommentInfo
import com.uplus.zip.domain.comment.exception.CommentErrorCode
import com.uplus.zip.domain.maps.domain.Apartment
import com.uplus.zip.domain.maps.domain.Dong
import com.uplus.zip.domain.maps.domain.Gu
import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.review.dao.ReviewRepository
import com.uplus.zip.domain.review.domain.Review
import com.uplus.zip.domain.review.exception.ReviewErrorCode
import com.uplus.zip.global.error.CustomException
import com.uplus.zip.global.util.S3Uploader
import com.uplus.zip.global.util.SecurityUtil
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockMultipartFile

class CommentServiceTest : BehaviorSpec({
    val commentRepository = mockk<CommentRepository>()
    val commentLikesRepository = mockk<CommentLikesRepository>()
    val reviewRepository = mockk<ReviewRepository>()
    val uploader = mockk<S3Uploader>()
    mockkObject(SecurityUtil.Companion)

    val commentService = CommentServiceImpl(
        commentRepository,
        commentLikesRepository,
        reviewRepository,
        uploader
    )
    val gu = Gu(guId = 1L, lat = 1.0, lng = 1.0, name = "gu")
    val apart = Apartment(
        si = "1",
        gu = gu,
        dong = Dong(dongId = 1L, lat = 1.0, lng = 1.0, name = "dong", gu = gu),
        bunzi = "bunzi",
        apartmentName = "name",
        builtYear = 1,
        lat = 1.0,
        lng = 1.0,
        apartmentId = 1L
    )
    val reviewId = 1L
    val memberId = 1L
    val commentId = 1L
    val startId = 20L
    val size = 20
    val pageRequest = PageRequest.ofSize(size + 1)
    val member = Member(kakaoId = "testId", nickname = "nick", memberId = memberId)
    val content = "test"
    val imageUrl = "http://test"
    val review = Review(
        apartment = apart,
        content = content,
        imageOriginUrl = imageUrl,
        imageThumbnailUrl = null,
        member = member,
        reviewId = reviewId
    )
    val comment =
        Comment(
            content = content,
            imageOriginUrl = imageUrl,
            imageThumbnailUrl = imageUrl,
            review = review,
            member = member,
            commentId = commentId
        )
    val commentLike = CommentLikes(member = member, comment = comment, commentLikesId = 1L)
    val commentProjectionMock = mockk<CommentInfo>()
    val comments = listOf(
        commentProjectionMock,
        commentProjectionMock
    )
    val page = PageImpl(comments)
    val commentCreateRequestDto = CommentCreateRequestDto(content = content, image = null)

    every { SecurityUtil.Companion.isLoginMember() } returns member
    every { SecurityUtil.Companion.getLoginMember() } returns member

    Given("특정 리뷰에 저장된 댓글이 N개 있는 상황에서") {
        every { reviewRepository.findByIdOrNull(any()) } returns review
        every { commentRepository.findAllByJpql(any(), any(), any(), any()) } returns page

        When("특정 리뷰에 달린 모든 댓글 리스트를 요청하면") {
            val result = commentService.getComments(reviewId, startId, size)

            Then("결과 리스트를 조회해야 한다") {
                verify(exactly = 1) {
                    commentRepository.findAllByJpql(reviewId, memberId, startId, pageRequest)
                }
            }

            And("조회된 결과 리스트에 N개의 댓글이 존재해야 한다") {
                result.page!!.size shouldBe 2
            }
        }
    }

    Given("리뷰가 존재하지 않는 상황에서") {
        every { reviewRepository.findByIdOrNull(any()) } returns null

        When("댓글 리스트를 요청하면") {
            Then("REVIEW_NOT_FOUND 예외가 발생해야 한다") {
                shouldThrow<CustomException> {
                    commentService.getComments(reviewId, startId, size)
                }.errorCode shouldBe ReviewErrorCode.REVIEW_NOT_FOUND
            }
        }

        When("댓글 생성을 요청하면") {
            Then("REVIEW_NOT_FOUND 예외가 발생해야 한다") {
                shouldThrow<CustomException> {
                    commentService.createComment(reviewId, commentCreateRequestDto)
                }.errorCode shouldBe ReviewErrorCode.REVIEW_NOT_FOUND
            }
        }
    }

    Given("댓글이 존재하는 상황에서") {
        every { commentRepository.findByIdOrNull(any()) } returns comment
        every { commentLikesRepository.findByMemberAndComment(any(), any()) } returns null
        every { commentLikesRepository.save(any()) } returns commentLike

        When("댓글 좋아요를 요청하면") {
            commentService.likeComment(commentId)

            Then("좋아요가 성공해야 한다") {
                verify(exactly = 1) { commentLikesRepository.save(any()) }
            }
        }
    }

    Given("댓글이 존재하고 이미 좋아요가 되어있는 상황에서") {
        every { commentRepository.findByIdOrNull(any()) } returns comment
        every { commentLikesRepository.findByMemberAndComment(any(), any()) } returns commentLike
        every { commentLikesRepository.delete(any()) } returns Unit

        When("댓글 좋아요를 요청하면") {
            commentService.likeComment(commentId)

            Then("좋아요가 취소되어야 한다") {
                verify(exactly = 1) { commentLikesRepository.delete(commentLike) }
            }
        }
    }

    Given("댓글이 존재하지 않는 상황에서") {
        every { commentRepository.findByIdOrNull(any()) } returns null
        every { commentLikesRepository.findByMemberAndComment(any(), any()) } returns null
        every { commentLikesRepository.save(any()) } returns commentLike

        When("댓글 좋아요를 요청하면") {
            Then("COMMENT_NOT_FOUND 예외가 발생해야 한다") {
                shouldThrow<CustomException> {
                    commentService.likeComment(commentId)
                }.errorCode shouldBe CommentErrorCode.COMMENT_NOT_FOUND
            }
        }

        When("댓글 삭제를 요청하면") {
            Then("COMMENT_NOT_FOUND 예외가 발생해야 한다") {
                shouldThrow<CustomException> {
                    commentService.likeComment(commentId)
                }.errorCode shouldBe CommentErrorCode.COMMENT_NOT_FOUND
            }
        }
    }

    Given("댓글이 존재하고 회원이 직접 작성한 댓글인 상황에서") {
        every { commentRepository.findByIdOrNull(any()) } returns comment
        every { commentRepository.delete(any()) } returns Unit
        every { uploader.deleteImage(any<String>()) } returns Unit

        When("댓글 삭제를 요청하면") {
            commentService.deleteComment(commentId)

            Then("댓글이 삭제되어야 한다") {
                verify(exactly = 1) { commentRepository.delete(comment) }
            }
        }
    }

    Given("회원이 직접 작성하지 않은 댓글을 삭제하려는 상황에서") {
        every { commentRepository.findByIdOrNull(any()) } returns Comment(
            content = content,
            imageOriginUrl = imageUrl,
            imageThumbnailUrl = null,
            review = review,
            member = Member(kakaoId = "kakao2", nickname = "nick", memberId = 2L),
            commentId = 2L
        )
        When("댓글 삭제를 요청하면") {
            Then("COMMENT_CANNOT_BE_DELETED 예외가 발생해야 한다") {
                shouldThrow<CustomException> {
                    commentService.deleteComment(commentId)
                }.errorCode shouldBe CommentErrorCode.COMMENT_CANNOT_BE_DELETED
            }
        }
    }

    Given("이미지 포함 댓글 요청과 리뷰가 존재하는 상황에서") {
        val filename = "test.jpg"
        val stream = "test content".byteInputStream()

        val image = MockMultipartFile(filename, filename, "image/jpeg", stream)

        val commentDtoWithImage = CommentCreateRequestDto(content, image)

        every { reviewRepository.findByIdOrNull(any()) } returns review
        every { uploader.uploadOrigin(any()) } returns imageUrl
        every { commentRepository.save(any()) } returns comment
        every { uploader.uploadResizing(any()) } returns imageUrl

        When("댓글 생성을 요청하면") {
            commentService.createComment(reviewId, commentDtoWithImage)

            Then("댓글이 생성되어야 한다") {
                verify(exactly = 1) { commentRepository.save(any()) }
            }

            And("S3에 이미지가 업로드 되어야 한다") {
                verify(exactly = 1) { uploader.uploadOrigin(commentDtoWithImage.image!!) }
            }
        }
    }
})