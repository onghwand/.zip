package com.uplus.zip.domain.comment.dao

import java.time.LocalDateTime

class MockCommentInfo(
    override val id: Long? = null,
    override val nickname: String = "Mock name",
    override val memberId: Long? = null,
    override val content: String = "Mock comment",
    override val imageOriginUrl: String? = null,
    override val imageThumbnailUrl: String? = null,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val likesCount: Int = 0,
    override val likedByCurrentUser: Boolean = false
) : CommentInfo