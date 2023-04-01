package com.uplus.zip.domain.maps.dao

import com.uplus.zip.domain.maps.domain.Dong
import org.springframework.data.jpa.repository.JpaRepository

interface DongRepository: JpaRepository<Dong, Long> {

    fun findByLatBetweenAndLngBetween(fromLat: Double, toLat: Double, fromLng: Double, toLng: Double): List<Dong>

}