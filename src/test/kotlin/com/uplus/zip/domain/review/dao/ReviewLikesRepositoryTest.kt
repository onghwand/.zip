package com.uplus.zip.domain.review.dao

import com.uplus.zip.domain.maps.dao.ApartmentRepository
import com.uplus.zip.domain.maps.dao.DongRepository
import com.uplus.zip.domain.maps.dao.GuRepository
import com.uplus.zip.domain.maps.domain.Apartment
import com.uplus.zip.domain.maps.domain.Dong
import com.uplus.zip.domain.maps.domain.Gu
import com.uplus.zip.domain.member.dao.MemberRepository
import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.review.domain.Review
import com.uplus.zip.domain.review.domain.ReviewLikes
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class ReviewLikesRepositoryTest(
    private val memberRepository: MemberRepository,
    private val apartmentRepository: ApartmentRepository,
    private val reviewRepository: ReviewRepository,
    private val reviewLikesRepository: ReviewLikesRepository,
    private val guRepository: GuRepository,
    private val dongRepository: DongRepository
) : BehaviorSpec({
    val member = Member("123123", "test", 1)
    val gu = Gu("gu", 12.12, 13.12, 1)
    guRepository.save(gu)
    val dong = Dong("dong", 12.21, 31.2, guRepository.findByIdOrNull(1)!!)
    dongRepository.save(dong)
    val apartment = Apartment(
        "si",
        gu,
        dong,
        "bunzi",
        "apart",
        1,
        12.12,
        32.32,
        1
    )
    apartmentRepository.save(apartment)
    val review = Review("content", null, null, member, apartment, mutableListOf(), mutableListOf(), 1)
    val reviewLikes = ReviewLikes(review, member, 1)
    memberRepository.save(member)
    reviewRepository.save(review)
    Given("리뷰와 멤버가 있고") {
        When("해당 리뷰의 멤버가 좋아요 버튼을 누른지 확인하고 싶을 때") {
            val result = reviewLikesRepository.findByReviewAndMember(review, member)
            Then("좋아요 여부가 반환되어야 된다.") {
                result shouldBeSameInstanceAs null
            }
        }
        When("해당 멤버가 좋아요를 누르면") {
            val cnt = reviewLikesRepository.findAll().size
            reviewLikesRepository.save(reviewLikes)
            Then("좋아요가 데이터가 저장되어야한다.") {
                reviewLikesRepository.findAll().size shouldBe cnt + 1
            }
        }
        When("해당 멤버가 좋아요를 취소하면") {
            reviewLikesRepository.delete(reviewLikes)
            Then("좋아요 데이터가 삭제되어야한다.") {
                reviewLikesRepository.findByIdOrNull(reviewLikes.reviewLikeId) shouldBe null
            }
        }
    }
    Given("리뷰가 있고") {
        When("해당 리뷰의 좋아요 수를 확인하고 싶을 떄") {
            reviewLikesRepository.save(reviewLikes)
            val result = reviewLikesRepository.countByReview(review)
            Then("좋아요 수가 반환되어야한다.") {
                result shouldBe 1
            }
        }
    }
})