package com.uplus.zip.domain.member.exception

import com.uplus.zip.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class MemberErrorCode(
    private val status: HttpStatus,
    private val message: String
) : ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 아닙니다. 회원가입을 해주세요."),
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "AccessToken이 없습니다."),
    ACCESS_TOKEN_NOT_VALIDATED(HttpStatus.UNAUTHORIZED, "AccessToken이 만료되었습니다."),
    REFRESH_TOKEN_NOT_VALIDATED(HttpStatus.UNAUTHORIZED, "RefreshToken이 만료되었습니다."),
    REFRESH_TOKEN_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "올바른 타입의 토큰이 아닙니다.")
    ;

    override fun getStatus(): HttpStatus {
        return status
    }

    override fun getMessage(): String {
        return message
    }

}