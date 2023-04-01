package com.uplus.zip.domain.maps.domain

import java.time.LocalDate
import javax.persistence.*

@Entity
class Contract(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id")
    val apartment: Apartment,

    @Column(nullable = false)
    val contractDate: LocalDate,

    @Column(nullable = false)
    val area: Double,

    @Column(nullable = false)
    val floor: Int,

    val deposit: Int,

    @Column(nullable = false)
    val price: Int,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: ContractType,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val contractId: Long? = null
) {
}