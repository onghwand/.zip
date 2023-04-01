package com.uplus.zip.domain.search.dao

interface SearchResult {
    val id: Long
    val name: String
    val address: String
    val yearBuilt: Int
    val lat: Double
    val lng: Double
}