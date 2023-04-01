package com.uplus.zip.domain.member.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*

@Entity
class Member(
    @Column(nullable = false, unique = true)
    val kakaoId: String,

    @Column(nullable = false)
    val nickname: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val memberId: Long? = null
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        Collections.singleton(SimpleGrantedAuthority("ROLE_MEMBER"))

    override fun getPassword(): String = kakaoId

    override fun getUsername(): String = memberId.toString()

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true


}