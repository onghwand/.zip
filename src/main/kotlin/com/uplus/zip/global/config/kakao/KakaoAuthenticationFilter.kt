package com.uplus.zip.global.config.kakao

import com.uplus.zip.global.config.security.CustomUserDetailsService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class KakaoAuthenticationFilter(
    private val customUserDetailsService: CustomUserDetailsService,
    private val kakaoService: KakaoService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.requestURI != "/api/users/login") {
            request.getHeader("authorization")
                .takeIf { it != null && it.startsWith("Bearer ") }?.let {
                    val accessToken = it.substring("Bearer ".length)
                    val kakaoId = kakaoService.validateAccessToken(accessToken)
                    if (kakaoId != null) {
                        val userDetails = customUserDetailsService.loadUserByKakaoId(kakaoId)
                        SecurityContextHolder.getContext().authentication =
                            UsernamePasswordAuthenticationToken(
                                userDetails,
                                userDetails.password,
                                userDetails.authorities
                            )
                    }
                }
        }
        filterChain.doFilter(request, response)
    }


}