package com.uplus.zip.domain.maps.dto

import org.springframework.web.bind.annotation.RequestParam

data class MapRequestParameterDto(
    @RequestParam
    val latNE: Double,

    @RequestParam
    val lngNE: Double,

    @RequestParam
    val latSW: Double,

    @RequestParam
    val lngSW: Double,

    @RequestParam
    val zoom: Double,

    @RequestParam
    val type: String,

    @RequestParam(required = false)
    val minArea: Double = 0.0,

    @RequestParam(required = false)
    val maxArea: Double = 400.0,

    @RequestParam(required = false)
    val minYear: Int = 0,

    @RequestParam(required = false)
    val maxYear: Int = 2024,

    @RequestParam(required = false)
    val minPrice: Int = 0,

    @RequestParam(required = false)
    val maxPrice: Int = 5000000
)