package com.uplus.zip.global.config.kakao

import com.uplus.zip.domain.member.dto.ReissueResponseDto
import com.uplus.zip.domain.member.exception.MemberErrorCode
import com.uplus.zip.global.error.CustomException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.time.LocalDateTime

@Service
class KakaoService {

    @Value("\${spring.security.oauth2.client_id}")
    private lateinit var clientId: String

    private val log = LoggerFactory.getLogger(KakaoService::class.java)

    fun validateAccessToken(accessToken: String): String? {
        val restTemplate = RestTemplate()
        val uri = URI.create("https://kapi.kakao.com/v1/user/access_token_info")

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }

        val request = HttpEntity<Map<String, Any>>(headers)

        try {
            val response = restTemplate.exchange(uri, HttpMethod.GET, request, Map::class.java)
            if (response.statusCode.is2xxSuccessful) {
                val expiresIn = response.body?.get("expires_in") as? Int
                if (expiresIn != null && expiresIn > 0)
                    return response.body!!.get("id").toString()
            }
        } catch (e: HttpClientErrorException) {
            log.error("[KAKAO] 토큰 검증 실패")
        }
        return null
    }

    fun reissueAccessToken(refreshToken: String): ReissueResponseDto {
        val restTemplate = RestTemplate()
        val uri = URI.create("https://kauth.kakao.com/oauth/token")

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val params = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "refresh_token")
            add("client_id", clientId)
            add("refresh_token", refreshToken)
        }

        val request = HttpEntity<MultiValueMap<String, String>>(params, headers)
        try {
            val response = restTemplate.postForEntity(uri, request, Map::class.java)
            return ReissueResponseDto(
                "Bearer ${response.body?.get("access_token")}",
                LocalDateTime.now().plusSeconds((response.body?.get("expires_in") as Int).toLong())
            )
        } catch (e: HttpClientErrorException) {
            log.error("[KAKAO] 재발급 실패")
            throw CustomException(MemberErrorCode.REFRESH_TOKEN_NOT_VALIDATED)
        }
    }

    fun logout(accessToken: String) {
        val restTemplate = RestTemplate()
        val uri = URI.create("https://kapi.kakao.com/v1/user/logout")

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }

        val request = HttpEntity<MultiValueMap<String, String>>(headers)

        try {
            restTemplate.exchange(uri, HttpMethod.POST, request, Map::class.java)
        } catch (e: HttpClientErrorException) {
            log.error("[KAKAO] 로그아웃 요청 실패")
            throw CustomException(MemberErrorCode.ACCESS_TOKEN_NOT_VALIDATED)
        }

    }


}