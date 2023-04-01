package com.uplus.zip.domain.maps.application

import com.uplus.zip.domain.maps.dto.MapInfoResponseDto
import com.uplus.zip.domain.maps.dto.MapRequestParameterDto

interface ApartmentService {

    fun getInfoList(mapRequestParameterDto: MapRequestParameterDto): MapInfoResponseDto
}