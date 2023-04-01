package com.uplus.zip.domain.search.domain

import javax.persistence.*

@Entity
class SearchDong(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val lat: Double,

    @Column(nullable = false)
    val lng: Double,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val dongId: Long? = null
)