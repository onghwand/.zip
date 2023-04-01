package com.uplus.zip.domain.comment.application

import com.uplus.zip.domain.comment.dto.request.CommentCreateRequestDto
import com.uplus.zip.domain.comment.dao.CommentInfo
import com.uplus.zip.global.common.dto.PaginationResponseDto

interface CommentService {
    fun createComment(
        reviewId: Long,
        commentCreateRequestDto: CommentCreateRequestDto
    )

    fun deleteComment(
        commentId: Long,
    )

    fun likeComment(
        commentId: Long,
    )

    fun getComments(
        reviewId: Long,
        startId: Long,
        size: Int
    ): PaginationResponseDto<CommentInfo>
}