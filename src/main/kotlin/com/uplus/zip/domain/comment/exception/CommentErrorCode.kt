package com.uplus.zip.domain.comment.exception

import com.uplus.zip.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class CommentErrorCode(private val status: HttpStatus, private val message: String) :
    ErrorCode {

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    COMMENT_CANNOT_BE_DELETED(HttpStatus.FORBIDDEN, "해당 댓글을 삭제할 권한이 없습니다.")
    ;

    override fun getStatus(): HttpStatus {
        return status
    }

    override fun getMessage(): String {
        return message
    }
}