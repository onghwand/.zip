package com.uplus.zip.domain.search.api

import com.uplus.zip.domain.search.application.SearchService
import com.uplus.zip.domain.search.dto.AutoCompleteResponseDto
import com.uplus.zip.domain.search.dto.SearchResultResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/search")
class SearchController(private val searchService: SearchService) {

    @GetMapping("/autocomplete")
    fun getAutoCompletes(@RequestParam(name = "keyword") keyword: String): ResponseEntity<AutoCompleteResponseDto> {
        return ResponseEntity(searchService.getAutoCompletes(keyword), HttpStatus.OK)
    }

    @GetMapping
    fun getSearchResults(@RequestParam(name = "keyword") keyword: String): ResponseEntity<SearchResultResponseDto> {
        return ResponseEntity(searchService.getSearchResults(keyword), HttpStatus.OK)
    }
}