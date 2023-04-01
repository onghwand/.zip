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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(value = [SpringExtension::class]) // 스프링 컨텍스트 로드
@DataJpaTest // 인메모리 DB 사용
class CommentRepositoryTest(
    private val commentLikesRepository: CommentLikesRepository,
    private val memberRepository: MemberRepository,
    private val apartmentRepository: ApartmentRepository,
    private val reviewRepository: ReviewRepository,
    private val commentRepository: CommentRepository,
    private val guRepository: GuRepository,
    private val dongRepository: DongRepository
) : BehaviorSpec({
    beforeEach {
        commentRepository.deleteAll()
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
    val member = memberRepository.save(Member(kakaoId = "kakao2", nickname = "nick1"))

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

    Given("회원, 댓글이 주어진 상황에서") {
        val expectedComment = Comment(
            content = "content1",
            imageOriginUrl = null,
            imageThumbnailUrl = null,
            member = member,
            review = review
        )
        When("save 함수를 실행하면") {
            val savedComment = commentRepository.save(expectedComment)
            Then("DB에 저장되어야 한다") {
                savedComment shouldBe expectedComment
            }
        }
    }

    Given("댓글이 저장되어 있는 상황에서") {
        val comment = Comment(
            content = "content1",
            imageOriginUrl = null,
            imageThumbnailUrl = null,
            member = member,
            review = review
        )
        val savedComment = commentRepository.save(comment)

        When("findByIdOrNull 함수를 실행하면") {
            val foundComment = commentRepository.findByIdOrNull(savedComment.commentId)

            Then("저장된 댓글이 조회되어야 한다") {
                foundComment!!.commentId shouldBe savedComment.commentId
            }
        }

        When("delete 함수를 실행하면") {
            commentRepository.delete(savedComment)

            Then("삭제되어야 한다") {
                val isCommentExists = commentRepository.existsById(savedComment.commentId!!)
                isCommentExists shouldBe false
            }
        }
    }

    Given("댓글이 저장되어 있지 않은 상황에서") {
        When("findByIdOrNull 함수를 실행하면") {
            val foundComment = commentRepository.findByIdOrNull(1L)

            Then("null이 반환되어야 한다") {
                foundComment shouldBe null
            }
        }
    }

    Given("회원, 댓글, 해당 댓글 좋아요가 주어진 상황에서") {
        val comment = Comment(
            content = "content1",
            imageOriginUrl = null,
            imageThumbnailUrl = null,
            member = member,
            review = review
        )
        val savedComment = commentRepository.save(comment)
        val commentLike = commentLikesRepository.save(CommentLikes(savedComment, member))
        When("댓글을 삭제하면") {
            commentRepository.delete(savedComment)

            Then("해당 댓글에 달린 좋아요도 삭제되어야 한다") {
                commentLikesRepository.findByIdOrNull(commentLike.commentLikesId) shouldBe null
            }
        }
    }
})