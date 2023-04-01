package com.uplus.zip.global.common.dto

data class PaginationMeta(
    val hasNext: Boolean,
    val total: Long = 0,
)