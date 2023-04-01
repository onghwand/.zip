package com.uplus.zip.global.error

import com.uplus.zip.global.common.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(CustomException::class)
    protected fun handlerCustomException(e: CustomException): ResponseEntity<ErrorResponse<Int>> {
        return ResponseEntity
            .status(e.errorCode.getStatus())
            .body(ErrorResponse(e.errorCode.getMessage()))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handlerMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse<Unit>> {
        val errorMessage = e.bindingResult.allErrors[0].defaultMessage
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(errorMessage))
    }

    @ExceptionHandler(BindException::class)
    protected fun handlerBindException(e: BindException): ResponseEntity<ErrorResponse<Unit>> {
        val errorMessage = e.bindingResult.allErrors[0].defaultMessage
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(errorMessage))
    }

    @ExceptionHandler(Exception::class)
    protected fun handlerAllExceptions(e: Exception): ResponseEntity<ErrorResponse<Unit>> {
        print(e.printStackTrace())
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse("서버 에러입니다."))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    protected fun handlerHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse("요청을 다시 확인해주세요."))
    }
}