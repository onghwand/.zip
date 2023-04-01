package com.uplus.zip.domain.member.application

import com.uplus.zip.domain.member.application.impl.MemberServiceImpl
import com.uplus.zip.domain.member.dao.MemberRepository
import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.member.dto.ReissueRequestDto
import com.uplus.zip.domain.member.dto.ReissueResponseDto
import com.uplus.zip.domain.member.exception.MemberErrorCode
import com.uplus.zip.global.config.kakao.KakaoService
import com.uplus.zip.global.error.CustomException
import com.uplus.zip.global.util.SecurityUtil
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.springframework.http.HttpHeaders
import java.time.LocalDateTime

class MemberServiceImplTest : BehaviorSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    val memberRepository = mockk<MemberRepository>()
    val kakaoService = mockk<KakaoService>()
    val memberService = MemberServiceImpl(memberRepository, kakaoService)

    init {

        val validHeader = HttpHeaders()
        validHeader.set("authorization", "Bearer validAccessToken")

        val inValidHeader = HttpHeaders()
        inValidHeader.set("authorization", "Bearer inValidAccessToken")

        val notBearerHeader = HttpHeaders()
        notBearerHeader.set("authorization", "NotBearerToken")

        every { kakaoService.validateAccessToken("validAccessToken") } returns "123"
        every { kakaoService.validateAccessToken("inValidAccessToken") } returns null
        every { kakaoService.logout("validAccessToken") } just Runs
        every { kakaoService.logout("inValidAccessToken") } throws CustomException(MemberErrorCode.ACCESS_TOKEN_NOT_VALIDATED)
        every { kakaoService.reissueAccessToken("validAccessToken") } returns ReissueResponseDto(
            "Bearer reissuedAccessToken",
            LocalDateTime.now().plusDays(3)
        )
        every { kakaoService.reissueAccessToken("inValidAccessToken") } throws CustomException(MemberErrorCode.REFRESH_TOKEN_NOT_VALIDATED)

        // 로그인 성공 테스트
        given("AccessToken 전달1") {
            `when`("첫 서비스 로그인(회원가입)일 때") {
                every { memberRepository.findByKakaoId("123") } returns null
                every { memberRepository.save(any()) } returns Member("123", "안쓰러운 기린", 1L)
                val memberLoginResponseDto = memberService.login(validHeader)

                `then`("성공") {
                    memberLoginResponseDto.memberId shouldNotBe null
                    memberLoginResponseDto.accessToken shouldBe "validAccessToken"
                }
            }
        }

        given("AccessToken 전달2") {
            `when`("회원가입된 유저일 경우") {
                every { memberRepository.findByKakaoId("123") } returns Member("123", "안쓰러운 기린", 1L)
                val memberLoginResponseDto = memberService.login(validHeader)

                `then`("성공") {
                    memberLoginResponseDto.memberId shouldNotBe null
                    memberLoginResponseDto.accessToken shouldBe "validAccessToken"
                }
            }
        }

        // 로그인 실패 테스트
        given("만료된 AccessToken") {
            `when`("로그인 요청") {
                `then`("실패") {
                    shouldThrow<CustomException> { memberService.login(inValidHeader) }
                        .errorCode shouldBe MemberErrorCode.ACCESS_TOKEN_NOT_VALIDATED
                }
            }
        }
        given("잘못된 형식의 AccessToken") {
            val headers = HttpHeaders()
            headers.set("authorization", "NotBearerToken")

            `when`("로그인 요청") {
                `then`("실패") {
                    shouldThrow<CustomException> { memberService.login(notBearerHeader) }
                        .errorCode shouldBe MemberErrorCode.ACCESS_TOKEN_NOT_FOUND
                }
            }
        }
        given("AccessToken을 보내지 않을 경우") {
            `when`("로그인 요청") {
                `then`("실패") {
                    shouldThrow<CustomException> { memberService.login(HttpHeaders()) }
                        .errorCode shouldBe MemberErrorCode.ACCESS_TOKEN_NOT_FOUND
                }
            }
        }


        // 로그아웃 성공 테스트
        given("유효한 AccessToken") {
            `when`("로그아웃 요청") {
                `then`("성공") {
                    shouldNotThrowAny {
                        memberService.logout(validHeader)
                    }
                }
            }
        }

        // 로그아웃 실패 테스트
        given("만료된 AccessToken") {
            `when`("로그아웃 요청") {
                `then`("실패") {
                    shouldThrow<CustomException> { memberService.logout(inValidHeader) }
                        .errorCode shouldBe MemberErrorCode.ACCESS_TOKEN_NOT_VALIDATED
                }
            }
        }
        given("잘못된 형식의 AccessToken") {
            `when`("로그아웃 요청") {
                `then`("실패") {
                    shouldThrow<CustomException> { memberService.logout(notBearerHeader) }
                        .errorCode shouldBe MemberErrorCode.ACCESS_TOKEN_NOT_FOUND
                }
            }
        }
        given("AccessToken을 보내지 않을 경우") {
            `when`("로그아웃 요청") {
                `then`("실패") {
                    shouldThrow<CustomException> { memberService.logout(HttpHeaders()) }
                        .errorCode shouldBe MemberErrorCode.ACCESS_TOKEN_NOT_FOUND
                }
            }
        }


        // 회원조회 성공 테스트
        given("로그인 시") {
            mockkObject(SecurityUtil.Companion)
            every { SecurityUtil.getLoginMember() } returns Member("123", "안쓰러운 기린", 1L)
            `when`("회원정보 요청") {
                val loginMemberInfo = memberService.getLoginMemberInfo()
                `then`("성공") {
                    loginMemberInfo.id shouldNotBe null
                    loginMemberInfo.nickname shouldNotBe null
                }
            }
        }


        // 재발급 성공 테스트
        given("유효한 RefreshToken") {
            val reissueRequestDto = ReissueRequestDto("Bearer validAccessToken")
            `when`("재발급 요청") {
                val reissueResponseDto = memberService.reissue(reissueRequestDto)
                `then`("성공") {
                    reissueResponseDto shouldNotBe null
                    reissueResponseDto.accessToken shouldNotBe null
                    reissueResponseDto.expiredAt shouldNotBe null
                }
            }
        }

        // 재발급 실패 테스트
        given("만료된 RefreshToken") {
            val reissueRequestDto = ReissueRequestDto("Bearer inValidAccessToken")
            `when`("재발급 요청") {
                `then`("실패") {
                    shouldThrow<CustomException> {
                        memberService.reissue(reissueRequestDto)
                    }.errorCode shouldBe MemberErrorCode.REFRESH_TOKEN_NOT_VALIDATED
                }
            }
        }
        given("잘못된 형식의 RefreshToken") {
            val reissueRequestDto = ReissueRequestDto("NotBearer")
            `when`("재발급 요청") {
                `then`("실패") {
                    shouldThrow<CustomException> {
                        memberService.reissue(reissueRequestDto)
                    }.errorCode shouldBe MemberErrorCode.REFRESH_TOKEN_NOT_AVAILABLE
                }
            }
        }
    }

}