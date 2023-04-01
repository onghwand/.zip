package com.uplus.zip.domain.maps.dto

import com.uplus.zip.domain.maps.dao.ApartmentInfoDao

data class MapInfoResponseDto(
    val pins: List<ApartmentInfoDao>
)
