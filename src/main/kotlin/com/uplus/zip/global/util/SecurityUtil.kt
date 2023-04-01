package com.uplus.zip.global.util

import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.member.exception.MemberErrorCode
import com.uplus.zip.global.error.CustomException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityUtil {

    companion object {
        lateinit var instance: SecurityUtil // companion object에서 생성된 객체를 저장할 변수

        fun init(instance: SecurityUtil) {
            this.instance = instance // 객체를 저장
        }

        fun getLoginMember(): Member {
            val authentication = SecurityContextHolder.getContext().authentication
                ?: throw CustomException(MemberErrorCode.MEMBER_NOT_FOUND)
            return authentication.principal as Member
        }

        fun isLoginMember(): Member? {
            val authentication = SecurityContextHolder.getContext().authentication
                ?: throw CustomException(MemberErrorCode.MEMBER_NOT_FOUND)
            return if (authentication.name == "anonymousUser")
                null
            else {
                authentication.principal as Member
            }
        }
    }

    init {
        init(this) // 객체가 생성될 때 init() 함수를 호출하여 instance 변수에 할당
    }
}

