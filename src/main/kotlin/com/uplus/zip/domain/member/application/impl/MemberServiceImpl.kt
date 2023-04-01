package com.uplus.zip.domain.member.application.impl

import com.uplus.zip.domain.member.application.MemberService
import com.uplus.zip.domain.member.dao.MemberRepository
import com.uplus.zip.domain.member.domain.Adjective
import com.uplus.zip.domain.member.domain.Animal
import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.member.dto.MemberInfoResponseDto
import com.uplus.zip.domain.member.dto.MemberLoginResponseDto
import com.uplus.zip.domain.member.dto.ReissueRequestDto
import com.uplus.zip.domain.member.dto.ReissueResponseDto
import com.uplus.zip.domain.member.exception.MemberErrorCode
import com.uplus.zip.global.config.kakao.KakaoService
import com.uplus.zip.global.error.CustomException
import com.uplus.zip.global.util.SecurityUtil
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
    private val kakaoService: KakaoService
) : MemberService {

    @Transactional
    override fun login(headers: HttpHeaders): MemberLoginResponseDto {
        val accessToken = headers.getFirst("authorization")
            .takeIf { it != null && it.startsWith("Bearer ") }
            ?.let { it.substring("Bearer ".length) }
            ?: throw CustomException(MemberErrorCode.ACCESS_TOKEN_NOT_FOUND)

        val kakaoId = kakaoService.validateAccessToken(accessToken)
            ?: throw CustomException(MemberErrorCode.ACCESS_TOKEN_NOT_VALIDATED)

        val member = memberRepository.findByKakaoId(kakaoId)
            ?: memberRepository.save(Member(kakaoId, makeRandomNickname()))

        return MemberLoginResponseDto(accessToken, member.memberId!!)
    }

    override fun logout(headers: HttpHeaders) {
        val accessToken = headers.getFirst("authorization")
            .takeIf { it != null && it.startsWith("Bearer ") }
            ?.let { it.substring("Bearer ".length) }
            ?: throw CustomException(MemberErrorCode.ACCESS_TOKEN_NOT_FOUND)
        kakaoService.logout(accessToken)
    }

    override fun getLoginMemberInfo(): MemberInfoResponseDto {
        val member = SecurityUtil.getLoginMember()
        return MemberInfoResponseDto.Companion.toDto(member)
    }

    override fun reissue(reissueRequestDto: ReissueRequestDto): ReissueResponseDto {
        reissueRequestDto.refreshToken.takeIf { it.startsWith("Bearer ") }
            ?.let {
                return kakaoService.reissueAccessToken(it.substring("Bearer ".length))
            }
        throw CustomException(MemberErrorCode.REFRESH_TOKEN_NOT_AVAILABLE)
    }

    protected fun makeRandomNickname(): String {
        return "${Adjective.getRandomAdjective()} ${Animal.getRandomAnimal()}"
    }

}