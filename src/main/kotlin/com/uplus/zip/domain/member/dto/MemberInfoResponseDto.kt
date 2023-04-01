package com.uplus.zip.domain.member.dto

import com.uplus.zip.domain.member.domain.Member

data class MemberInfoResponseDto(
    val id: Long,
    val nickname: String
) {

    companion object {
        fun toDto(member: Member): MemberInfoResponseDto =
            MemberInfoResponseDto(member.memberId!!, member.nickname)
    }
}
