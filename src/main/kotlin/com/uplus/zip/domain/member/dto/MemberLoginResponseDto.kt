package com.uplus.zip.domain.member.dto

data class MemberLoginResponseDto(
    val accessToken: String,
    val memberId: Long
)