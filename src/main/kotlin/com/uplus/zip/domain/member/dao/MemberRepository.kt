package com.uplus.zip.domain.member.dao

import com.uplus.zip.domain.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {

    fun findByKakaoId(kakaoId: String): Member?

}