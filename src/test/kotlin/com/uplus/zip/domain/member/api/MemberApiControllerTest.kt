package com.uplus.zip.domain.member.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.uplus.zip.domain.member.application.MemberService
import com.uplus.zip.domain.member.dto.*
import com.uplus.zip.domain.member.exception.MemberErrorCode
import com.uplus.zip.global.error.CustomException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(MemberApiController::class)
@AutoConfigureMockMvc(addFilters = false)
class MemberApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @DisplayName("로그인 성공")
    @Test
    fun loginApiSuccess() {
        val httpHeaders = HttpHeaders()
        httpHeaders.set("authorization", "Bearer validAccessToken")
        `when`(memberService.login(httpHeaders)).thenReturn(MemberLoginResponseDto("validAccessToken", 10))

        mockMvc.perform(
            post("/users/login")
                .header("authorization", "Bearer validAccessToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value("validAccessToken"))
            .andExpect(jsonPath("$.memberId").value(10))
    }

    @DisplayName("만료된 토큰 전달 시 로그인 실패")
    @Test
    fun loginApiFailWithInvalidAccessToken() {
        val httpHeaders = HttpHeaders()
        httpHeaders.set("authorization", "Bearer inValidAccessToken")
        `when`(memberService.login(httpHeaders)).thenThrow(CustomException(MemberErrorCode.ACCESS_TOKEN_NOT_VALIDATED))

        mockMvc.perform(
            post("/users/login")
                .header("authorization", "Bearer inValidAccessToken")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value(MemberErrorCode.ACCESS_TOKEN_NOT_VALIDATED.getMessage()))
    }

    @DisplayName("로그아웃 성공")
    @Test
    fun logoutApiSuccess() {
        val httpHeaders = HttpHeaders()
        httpHeaders.set("authorization", "Bearer validAccessToken")
        doNothing().`when`(memberService).logout(httpHeaders)

        mockMvc.perform(
            post("/users/logout")
                .header("authorization", "Bearer validAccessToken")
        )
            .andExpect(status().isOk)
    }

    @DisplayName("만료된 토큰 전달 시 로그아웃 실패")
    @Test
    fun logoutApiFailWithInvalidAccessToken() {
        val httpHeaders = HttpHeaders()
        httpHeaders.set("authorization", "Bearer inValidAccessToken")
        `when`(memberService.logout(httpHeaders)).thenThrow(CustomException(MemberErrorCode.ACCESS_TOKEN_NOT_VALIDATED))

        mockMvc.perform(
            post("/users/logout")
                .header("authorization", "Bearer inValidAccessToken")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value(MemberErrorCode.ACCESS_TOKEN_NOT_VALIDATED.getMessage()))
    }

    @DisplayName("회원정보 조회 성공")
    @Test
    fun memberInfoApiSuccess() {
        `when`(memberService.getLoginMemberInfo()).thenReturn(MemberInfoResponseDto(10, "귀여운 돌고래"))

        mockMvc.perform(
            get("/users/me")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.nickname").value("귀여운 돌고래"))
    }

    @DisplayName("AccessToken 재발급 성공")
    @Test
    fun reissueApiSuccess() {
        val request = ReissueRequestDto("Bearer validRefreshToken")
        `when`(memberService.reissue(request))
            .thenReturn(ReissueResponseDto("reissuedAccessToken", LocalDateTime.now().plusSeconds(80000)))

        mockMvc.perform(
            post("/users/reissue")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.accessToken").value("reissuedAccessToken"))
            .andExpect(jsonPath("$.expiredAt").isNotEmpty)
    }

    @DisplayName("올바른 형식이 아닌 토큰 전달 시 재발급 성공")
    @Test
    fun reissueApiFailWithNotBearerType() {
        val request = ReissueRequestDto("refreshToken")
        `when`(memberService.reissue(request))
            .thenThrow(CustomException(MemberErrorCode.REFRESH_TOKEN_NOT_AVAILABLE))

        mockMvc.perform(
            post("/users/reissue")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value(MemberErrorCode.REFRESH_TOKEN_NOT_AVAILABLE.getMessage()))
    }

    @DisplayName("만료된 RefreshToken 전달 시 재발급 실패")
    @Test
    fun reissueApiFailWithInvalidRefreshToken() {
        val request = ReissueRequestDto("Bearer inValidRefreshToken")
        `when`(memberService.reissue(request))
            .thenThrow(CustomException(MemberErrorCode.REFRESH_TOKEN_NOT_VALIDATED))

        mockMvc.perform(
            post("/users/reissue")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value(MemberErrorCode.REFRESH_TOKEN_NOT_VALIDATED.getMessage()))
    }

    @DisplayName("refrshToken 없이 재발급 요청 시 실패")
    @Test
    fun reissueApiFailWithoutRefreshToken() {
        mockMvc.perform(
            post("/users/reissue")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("요청을 다시 확인해주세요."))
    }

}