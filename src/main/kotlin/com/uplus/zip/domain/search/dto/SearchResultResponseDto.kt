package com.uplus.zip.domain.search.dto

import com.uplus.zip.domain.search.dao.SearchResult

data class SearchResultResponseDto(
    val searchResults: List<SearchResult>
)