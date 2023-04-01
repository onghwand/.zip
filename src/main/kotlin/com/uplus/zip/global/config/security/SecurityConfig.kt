package com.uplus.zip.global.config.security

import com.uplus.zip.global.config.kakao.KakaoAuthenticationEntryPoint
import com.uplus.zip.global.config.kakao.KakaoConfig
import com.uplus.zip.global.config.kakao.KakaoService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val kakaoService: KakaoService,
    private val kakaoAuthenticationEntryPoint: KakaoAuthenticationEntryPoint
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf().disable()
        .cors()

        .and()
        .httpBasic().disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()
        .exceptionHandling()
        .authenticationEntryPoint(kakaoAuthenticationEntryPoint)

        .and()
        .authorizeRequests()
        .antMatchers("/users/login").permitAll()
        .antMatchers("/users/reissue").permitAll()
        .antMatchers("/maps/**").permitAll()
        .antMatchers(HttpMethod.GET, "/reviews/**/comments").permitAll()
        .antMatchers(HttpMethod.GET, "/reviews/**").permitAll()
        .antMatchers(HttpMethod.GET, "/search/**").permitAll()
        .anyRequest().authenticated()

        .and()
        .apply(KakaoConfig(customUserDetailsService, kakaoService))
        .and()
        .build()

}