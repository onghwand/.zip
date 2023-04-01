package com.uplus.zip.domain.search.dao

class MockSearchResult(
    override val id: Long = 1L,
    override val name: String = "마곡아파트",
    override val address: String = "서울특별시 마곡동 마곡아파트",
    override val yearBuilt: Int = 1996,
    override val lat: Double = 1.0,
    override val lng: Double = 1.0
) : SearchResult