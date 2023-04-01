package com.uplus.zip.domain.maps.dao

import org.springframework.stereotype.Repository

@Repository
interface ApartmentInfoDao {
    val id: Long
    val title: String
    val price: Double
    val deposit: Double
    val lat: Double
    val lng: Double
}