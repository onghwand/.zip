package com.uplus.zip.domain.member.dto

import java.time.LocalDateTime

data class ReissueResponseDto(
    val accessToken: String,
    val expiredAt: LocalDateTime
)
