package com.uplus.zip.domain.member.application

import com.uplus.zip.domain.member.dto.MemberInfoResponseDto
import com.uplus.zip.domain.member.dto.MemberLoginResponseDto
import com.uplus.zip.domain.member.dto.ReissueRequestDto
import com.uplus.zip.domain.member.dto.ReissueResponseDto
import org.springframework.http.HttpHeaders

interface MemberService {

    fun login(headers: HttpHeaders): MemberLoginResponseDto
    fun logout(headers: HttpHeaders)
    fun getLoginMemberInfo(): MemberInfoResponseDto
    fun reissue(reissueRequestDto: ReissueRequestDto): ReissueResponseDto
}