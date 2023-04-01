package com.uplus.zip.domain.review.dto

import java.time.LocalDateTime

data class ReviewInfoResponseDto(
    val id: Long?,
    val createdAt: LocalDateTime,
    val content: String,
    val imageOriginUrl: String?,
    val imageThumbnailUrl: String?,
    val memberId: Long?,
    val nickname: String,
    val commentCnt: Int,
    val reviewLikeCnt: Int,
    val likeStatus: Int
)