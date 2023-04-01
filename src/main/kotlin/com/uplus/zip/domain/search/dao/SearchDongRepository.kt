package com.uplus.zip.domain.search.dao

import com.uplus.zip.domain.search.domain.SearchDong
import org.springframework.data.jpa.repository.JpaRepository

interface SearchDongRepository : JpaRepository<SearchDong, Long> {
    fun findTop3ByNameContains(keyword: String): List<AutoComplete>
}