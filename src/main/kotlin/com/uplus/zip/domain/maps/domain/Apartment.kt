package com.uplus.zip.domain.maps.domain

import javax.persistence.*

@Entity
class Apartment(
    @Column(nullable = false)
    val si: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gu_id")
    val gu: Gu,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dong_id")
    val dong: Dong,

    @Column(nullable = false)
    val bunzi: String,

    @Column(nullable = false)
    val apartmentName: String,

    @Column(nullable = false)
    val builtYear: Int,

    @Column(nullable = false)
    val lat: Double,

    @Column(nullable = false)
    val lng: Double,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val apartmentId: Long? = null
) {

}