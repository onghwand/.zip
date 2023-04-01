package com.uplus.zip.domain.comment.dao

import java.time.LocalDateTime

interface CommentInfo {
    val id: Long?
    val nickname: String
    val memberId: Long?
    val content: String
    val imageOriginUrl: String?
    val imageThumbnailUrl: String?
    val createdAt: LocalDateTime
    val likesCount: Int
    val likedByCurrentUser: Boolean
}