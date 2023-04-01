package com.uplus.zip.domain.comment.dto.request

import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank

data class CommentCreateRequestDto(
    @field:NotBlank(message = "내용을 입력하세요.")
    val content: String,
    val image: MultipartFile?
)
