package com.uplus.zip.domain.maps.exception

import com.uplus.zip.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class ApartmentErrorCode(
    private val status: HttpStatus,
    private val message: String

) : ErrorCode {
    APARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "아파트 정보를 찾을 수 없습니다."),
    ;

    override fun getStatus(): HttpStatus {
        return status
    }

    override fun getMessage(): String {
        return message
    }
}