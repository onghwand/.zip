package com.uplus.zip.domain.comment.dao

import com.uplus.zip.domain.comment.domain.Comment
import com.uplus.zip.domain.comment.domain.CommentLikes
import com.uplus.zip.domain.maps.dao.ApartmentRepository
import com.uplus.zip.domain.maps.dao.DongRepository
import com.uplus.zip.domain.maps.dao.GuRepository
import com.uplus.zip.domain.maps.domain.Apartment
import com.uplus.zip.domain.maps.domain.Dong
import com.uplus.zip.domain.maps.domain.Gu
import com.uplus.zip.domain.member.dao.MemberRepository
import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.review.dao.ReviewRepository
import com.uplus.zip.domain.review.domain.Review
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(value = [SpringExtension::class]) // 스프링 컨텍스트 로드
@DataJpaTest // 인메모리 DB 사용
class CommentLikesRepositoryTest(
    private val commentLikesRepository: CommentLikesRepository,
    private val memberRepository: MemberRepository,
    private val apartmentRepository: ApartmentRepository,
    private val reviewRepository: ReviewRepository,
    private val commentRepository: CommentRepository,
    private val guRepository: GuRepository,
    private val dongRepository: DongRepository
) : BehaviorSpec({
    beforeEach {
        commentLikesRepository.deleteAll()
    }

    val gu = guRepository.save(Gu(lat = 1.0, lng = 1.0, name = "gu"))
    val dong = dongRepository.save(Dong(lat = 1.0, lng = 1.0, name = "dong", gu = gu))
    val apart = apartmentRepository.save(
        Apartment(
            si = "1",
            gu = gu,
            dong = dong,
            bunzi = "bunzi",
            apartmentName = "name",
            builtYear = 1,
            lat = 1.0,
            lng = 1.0
        )
    )
    val member = memberRepository.save(Member(kakaoId = "kakao1", nickname = "nick1"))
    val review =
        reviewRepository.save(
            Review(
                apartment = apart,
                content = "content1",
                imageOriginUrl = null,
                imageThumbnailUrl = null,
                member = member
            )
        )
    val comment =
        commentRepository.save(
            Comment(
                content = "content1",
                imageOriginUrl = null,
                imageThumbnailUrl = null,
                member = member,
                review = review
            )
        )

    Given("회원, 댓글이 주어진 상황에서") {
        val expectedCommentLike = CommentLikes(member = member, comment = comment)

        When("save 함수를 실행하면") {
            val savedCommentLike = commentLikesRepository.save(expectedCommentLike)

            Then("DB에 저장되어야 한다") {
                savedCommentLike shouldBe expectedCommentLike
                commentLikesRepository.deleteAll()
            }
        }
    }

    Given("회원, 댓글이 주어지고 이미 좋아요를 한 상황에서") {
        val commentLike = commentLikesRepository.save(CommentLikes(member = member, comment = comment))

        When("findByMemberAndComment 함수를 실행하면") {
            val foundCommentLike = commentLikesRepository.findByMemberAndComment(member, comment)

            Then("댓글 좋아요가 조회되어야 한다") {
                foundCommentLike!!.commentLikesId shouldBe commentLike.commentLikesId
            }
        }

        When("delete 함수를 실행하면") {
            commentLikesRepository.delete(commentLike)

            Then("DB에서 삭제되어야 한다") {
                val isCommentLikeExists = commentLikesRepository.existsById(commentLike.commentLikesId!!)
                isCommentLikeExists shouldBe false
                commentLikesRepository.deleteAll()
            }
        }
    }
})
