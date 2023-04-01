package com.uplus.zip.domain.review.exception

import com.uplus.zip.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class ReviewErrorCode(
    private val status: HttpStatus,
    private val message: String
) : ErrorCode {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 리뷰를 지울 수 없습니다. 작성자만 리뷰를 지울 수 있습니다."),
    ;

    override fun getStatus(): HttpStatus {
        return status
    }

    override fun getMessage(): String {
        return message
    }
}