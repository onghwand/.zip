package com.uplus.zip.domain.review.dao

import com.uplus.zip.domain.comment.domain.Comment
import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.review.domain.ReviewLikes
import java.time.LocalDateTime

interface ImageDao {
    val reviewId: Long
    val imageOriginUrl: String?
    val imageThumbnailUrl: String?
    val content: String
    val createdAt: LocalDateTime
    val member: Member
    val comments: List<Comment>
    val reviewLikes: List<ReviewLikes>
}