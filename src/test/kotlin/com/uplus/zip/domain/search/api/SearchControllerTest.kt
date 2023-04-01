package com.uplus.zip.domain.search.api

import com.uplus.zip.domain.search.application.SearchService
import com.uplus.zip.domain.search.dao.MockAutoComplete
import com.uplus.zip.domain.search.dao.MockSearchResult
import com.uplus.zip.domain.search.dto.AutoCompleteResponseDto
import com.uplus.zip.domain.search.dto.SearchResultResponseDto
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(SearchController::class)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var searchService: SearchService

    @Test
    fun `검색어 자동완성 성공`() {
        val keyword = "keyword"
        val apartmentAutoCompletes = List(7) { MockAutoComplete() }
        val dongAutoCompletes = List(3) { MockAutoComplete() }
        val autoCompleteResponseDto = AutoCompleteResponseDto(apartmentAutoCompletes, dongAutoCompletes)

        `when`(searchService.getAutoCompletes(keyword)).thenReturn(autoCompleteResponseDto)

        mockMvc.perform(
            get("/search/autocomplete?keyword=${keyword}")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.apartments.length()").value(7))
            .andExpect(jsonPath("$.dongs.length()").value(3))
    }

    @Test
    fun `검색 완료 성공`() {
        val keyword = "keyword"
        val apartmentSearchResponse = List(10) { MockSearchResult() }
        val searchResultResponseDto = SearchResultResponseDto(apartmentSearchResponse)

        `when`(searchService.getSearchResults(keyword)).thenReturn(searchResultResponseDto)

        mockMvc.perform(
            get("/search?keyword=${keyword}")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.searchResults.length()").value(10))
    }
}
