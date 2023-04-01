package com.uplus.zip.domain.maps.domain

import javax.persistence.*

@Entity
class Dong(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val lat: Double,

    @Column(nullable = false)
    val lng: Double,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gu_id")
    val gu: Gu,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val dongId: Long? = null
) {
}