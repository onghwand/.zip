package com.uplus.zip.global.error

import org.springframework.http.HttpStatus

interface ErrorCode {

    fun getStatus(): HttpStatus
    fun getMessage(): String

}