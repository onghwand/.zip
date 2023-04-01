package com.uplus.zip.domain.review.application

import com.uplus.zip.domain.maps.exception.ApartmentErrorCode
import com.uplus.zip.domain.member.exception.MemberErrorCode
import com.uplus.zip.domain.review.dao.ReviewRepository
import com.uplus.zip.domain.review.domain.Review
import com.uplus.zip.domain.review.dto.ReviewCreateRequestDto
import com.uplus.zip.domain.review.dto.ReviewInfoResponseDto
import com.uplus.zip.global.common.dto.PaginationResponseDto
import com.uplus.zip.global.error.CustomException
import com.uplus.zip.global.util.S3Uploader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ReviewServiceTest : BehaviorSpec({
    val reviewRepository = mockk<ReviewRepository>()
    val reviewService = mockk<ReviewService>()

    Given("로그인 한 사용자가 정상적으로 작성한 리뷰가 존재하고") {
        val review = mockk<Review>()
        val reviewCreateRequestDto = mockk<ReviewCreateRequestDto>()
        val reviewInfo = mockk<ReviewInfoResponseDto>()
        val pagination = mockk<PaginationResponseDto<ReviewInfoResponseDto>>()
        val uploader = mockk<S3Uploader>()

        every { reviewRepository.save(review) } answers { review }
        every { reviewService.createReview(reviewCreateRequestDto, 1) } answers { reviewRepository.save(review) }
        every {
            reviewService.getReviews(
                1,
                Long.MAX_VALUE,
                20
            )
        } returns pagination
        every {
            reviewService.getReview(
                1
            )
        } returns reviewInfo
        every {
            reviewService.createReview(
                reviewCreateRequestDto,
                -1
            )
        } throws CustomException(ApartmentErrorCode.APARTMENT_NOT_FOUND)
        every { reviewRepository.delete(review) } just Runs
        every { reviewRepository.findByIdOrNull(1) } returns review
        every { uploader.deleteImage("filePath") } just Runs
        every { reviewService.deleteReview(1) } answers {
            reviewRepository.delete(review)
            reviewRepository.findByIdOrNull(1)
            uploader.deleteImage("filePath")
        }

        When("해당 리뷰를 저장하려고 하면") {
            reviewService.createReview(reviewCreateRequestDto, 1)
            Then("리뷰 레포지토리 저장 로직을 호출해야한다.") {
                verify(exactly = 1) {
                    reviewRepository.save(review)
                }
            }
        }
        When("해당 리뷰를 커서기반 페이지네이션 조회하려고 하면") {
            val reviews = reviewService.getReviews(1, Long.MAX_VALUE, 20)
            Then("리뷰 페이지들이 조회되어야한다.") {
                reviews shouldBeSameInstanceAs pagination
            }
        }

        When("해당 리뷰를 상세 조회하려고 하면") {
            val review = reviewService.getReview(1)
            Then("리뷰 상세 정보가 조회되어야한다.") {
                review shouldBeSameInstanceAs reviewInfo
            }
        }

        When("해당 리뷰를 삭제하려고 하면") {
            reviewService.deleteReview(1)
            Then("해당 리뷰가 존재하는지 확인해야한다.") {
                verify(exactly = 1) {
                    reviewRepository.findByIdOrNull(1)
                }
            }
            Then("리뷰 레포지토리 삭제 로직을 호출해야한다.") {
                verify(exactly = 1) {
                    reviewRepository.delete(review)
                }
            }
            Then("이미지도 삭제되어야 한다.") {
                verify(exactly = 1) {
                    uploader.deleteImage("filePath")
                }
            }
        }
        When("존재하지 않는 아파트에 리뷰 작성을 하려고 하면") {
            Then("404 NOT FOUND 에러가 발생해야한다.") {
                shouldThrow<CustomException> {
                    reviewService.createReview(reviewCreateRequestDto, -1)
                }.errorCode shouldBe ApartmentErrorCode.APARTMENT_NOT_FOUND
            }
        }
    }

    Given("로그인을 하지 않은 사용자가") {
        val reviewCreateRequestDto = ReviewCreateRequestDto("내용", null)
        val reviewInfo = ReviewInfoResponseDto(1, LocalDateTime.now(), "글 내용", null, null, 1, "사용자", 1, 1, 0)
        every {
            reviewService.createReview(
                reviewCreateRequestDto,
                1
            )
        } throws CustomException(MemberErrorCode.MEMBER_NOT_FOUND)
        every { reviewService.changeReviewLike(1) } throws CustomException(MemberErrorCode.MEMBER_NOT_FOUND)
        every { reviewService.getReview(1) } returns reviewInfo
        When("해당 리뷰를 생성하려고 하면") {
            Then("MEMBER NOT FOUND 에러를 반환한다.") {
                shouldThrow<CustomException> {
                    reviewService.changeReviewLike(1)
                }.errorCode shouldBe MemberErrorCode.MEMBER_NOT_FOUND
            }
        }
        When("해당 리뷰를 좋아요하려고 하면") {
            Then("MEMBER NOT FOUND 에러를 반환한다.") {
                shouldThrow<CustomException> {
                    reviewService.changeReviewLike(1)
                }.errorCode shouldBe MemberErrorCode.MEMBER_NOT_FOUND
            }
        }
        When("리뷰 조회를 하려고 하면") {
            Then("좋아요 누른 여부는 항상 0이여야한다.") {
                reviewInfo.likeStatus shouldBe 0
            }
        }
    }
})