package com.uplus.zip.domain.member.dto

import javax.validation.constraints.NotBlank

data class ReissueRequestDto(

    @field:NotBlank(message = "refreshToken이 필요합니다.")
    val refreshToken: String

)
