package com.uplus.zip.domain.maps.exception

import com.uplus.zip.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class MapErrorCode(
    private val status: HttpStatus,
    private val message: String
) : ErrorCode {

    DONG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 동을 확인할 수 없습니다."),
    INVALID_CONTRACT_TYPE(HttpStatus.BAD_REQUEST, "존재하지 않는 타입입니다.")
    ;

    override fun getStatus(): HttpStatus {
        return status
    }

    override fun getMessage(): String {
        return message
    }
}