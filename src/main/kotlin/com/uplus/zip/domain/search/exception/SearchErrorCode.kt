package com.uplus.zip.domain.search.exception

import com.uplus.zip.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class SearchErrorCode(private val status: HttpStatus, private val message: String) :
    ErrorCode {

    KEYWORD_IS_REQUIRED(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요."),
    ;

    override fun getStatus(): HttpStatus {
        return status
    }

    override fun getMessage(): String {
        return message
    }
}