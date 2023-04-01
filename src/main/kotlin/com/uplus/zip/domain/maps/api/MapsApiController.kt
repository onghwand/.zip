package com.uplus.zip.domain.maps.api

import com.uplus.zip.domain.maps.application.ApartmentService
import com.uplus.zip.domain.maps.dto.MapRequestParameterDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/maps")
@RestController
class MapsApiController(private val apartmentService: ApartmentService) {

    @GetMapping()
    fun getApartmentInfoListV1(mapRequestParameterDto: MapRequestParameterDto): ResponseEntity<Any> {
        return ResponseEntity.ok(apartmentService.getInfoList(mapRequestParameterDto))
    }

}