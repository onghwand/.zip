package com.uplus.zip.global.config.security

import com.uplus.zip.domain.member.dao.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService(private val memberRepository: MemberRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return (memberRepository.findByIdOrNull(username.toLong())
            ?: throw UsernameNotFoundException("not found member"))
    }

    fun loadUserByKakaoId(kakaoId: String): UserDetails {
        return (memberRepository.findByKakaoId(kakaoId)
            ?: throw UsernameNotFoundException("not found member"))
    }
}