package com.uplus.zip.domain.maps.dao

import com.uplus.zip.domain.maps.domain.Gu
import org.springframework.data.jpa.repository.JpaRepository

interface GuRepository : JpaRepository<Gu, Long> {

    fun findByLatBetweenAndLngBetween(fromLat: Double, toLat: Double, fromLng: Double, toLng: Double): List<Gu>

}