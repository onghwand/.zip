package com.uplus.zip.domain.maps.domain

import javax.persistence.*

@Entity
class Gu (
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val lat: Double,

    @Column(nullable = false)
    val lng: Double,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val guId: Long? = null
) {
}