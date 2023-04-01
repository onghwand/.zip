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
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class ReviewRepositoryTest(
    private val memberRepository: MemberRepository,
    private val apartmentRepository: ApartmentRepository,
    private val reviewRepository: ReviewRepository,
    @Autowired private val guRepository: GuRepository,
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
    val pageable = PageRequest.of(1, 20)
    memberRepository.save(member)
    Given("데이터베이스에 리뷰가 있고") {
        reviewRepository.save(review)
        When("해당 리뷰를 조회하고 싶다면") {
            val result = reviewRepository.findByIdOrNull(review.reviewId)
            Then("리뷰를 리턴해야한다.") {
                result?.reviewId shouldBe  review.reviewId
            }
        }
        When("해당 리뷰를 삭제하고 싶다면") {
            reviewRepository.delete(review)
            Then("리뷰가 삭제되어야한다.") {
                reviewRepository.findByIdOrNull(review.reviewId) shouldBe null
            }
        }
        reviewRepository.save(Review("132", "123", "231", member, apartment, mutableListOf(), mutableListOf(),))
        When("이미지만 있는 리뷰를 들고오고 싶다면") {
            val result:Page<ImageDao> =
                reviewRepository.findByApartmentAndReviewIdLessThanAndImageOriginUrlNotNullOrderByCreatedAtDesc(
                    apartment,
                    Long.MAX_VALUE,
                    pageable
                )
            Then("반환되는 리뷰들은 이미지가 존재해야한다.") {
                result.forEach{page -> page.imageOriginUrl shouldNotBe null}
            }
        }
    }
    Given("사용자가 작성한 리뷰가 있고") {
        val currentReview= Review("review", null, null, member, apartment, mutableListOf(), mutableListOf())
        When("리뷰를 저장하고 싶다면") {
            val cnt = reviewRepository.findAll().size
            reviewRepository.save(currentReview)
            Then("저장되어야한다.") {
                reviewRepository.findAll().size shouldBe cnt+1
            }
        }
    }
})