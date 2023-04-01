package com.uplus.zip.domain.search.dto

import com.uplus.zip.domain.search.dao.AutoComplete

data class AutoCompleteResponseDto(
    val apartments: List<AutoComplete>,
    val dongs: List<AutoComplete>
)