package com.uplus.zip.global.common.dto

data class PaginationResponseDto<T>(
    val page: List<T>?,
    val meta: PaginationMeta
)