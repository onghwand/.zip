package com.uplus.zip.domain.search.application

import com.uplus.zip.domain.search.dto.AutoCompleteResponseDto
import com.uplus.zip.domain.search.dto.SearchResultResponseDto
import org.springframework.stereotype.Service

@Service
interface SearchService {
    fun getAutoCompletes(keyword: String): AutoCompleteResponseDto
    fun getSearchResults(keyword: String): SearchResultResponseDto
}