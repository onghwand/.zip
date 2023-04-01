package com.uplus.zip.global.config.kakao

import com.uplus.zip.global.config.security.CustomUserDetailsService
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class KakaoConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val kakaoService: KakaoService
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(builder: HttpSecurity?) {
        builder
            ?.addFilterBefore(
                KakaoAuthenticationFilter(customUserDetailsService, kakaoService),
                UsernamePasswordAuthenticationFilter::class.java
            )
    }
}