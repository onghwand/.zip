package com.uplus.zip.domain.member.dao

import com.uplus.zip.domain.member.domain.Member
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException

@DataJpaTest
class MemberRepositoryTest: BehaviorSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var memberRepository: MemberRepository

    init {
        given("Member Entity 생성") {
            val member = Member("1", "테스트 닉네임")

            `when`("생성한 Member Entity를 저장") {
                val savedMember = memberRepository.save(member)

                then("Entity 저장 성공") {
                    savedMember.kakaoId shouldBe member.kakaoId
                    savedMember.nickname shouldBe member.nickname
                }
            }

            `when`("kakao_id가 중복된 Member Entity 저장") {
                val newMember = Member("1", "테스트 닉네임2")

                `then`("중복된 kakao_id 저장 실패") {
                    shouldThrow<DataIntegrityViolationException> {
                        memberRepository.save(newMember)
                    }
                }
            }

            `when`("kakaoId가 1인 Member 조회") {
                val findMember = memberRepository.findByKakaoId("1")

                then("조회 성공") {
                    findMember shouldNotBe null
                    findMember?.kakaoId shouldBe "1"
                }
            }
        }

    }
}

