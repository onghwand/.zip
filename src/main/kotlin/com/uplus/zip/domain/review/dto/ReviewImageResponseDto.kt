package com.uplus.zip.domain.review.dto

import java.time.LocalDateTime

data class ReviewImageResponseDto(
    val id: Long,
    val imageOriginUrl: String?,
    val imageThumbnailUrl: String?,
    val content: String,
    val createdAt: LocalDateTime,
    val memberId: Long?,
    val nickname: String,
    val reviewLikeCnt: Int,
    val commentCnt: Int,
    val likeStatus: Int,
)