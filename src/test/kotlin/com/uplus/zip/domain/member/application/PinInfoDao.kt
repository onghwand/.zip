package com.uplus.zip.domain.member.application

import com.uplus.zip.domain.maps.dao.ApartmentInfoDao

class PinInfoDao(
    override val id: Long,
    override val title: String,
    override val price: Double,
    override val deposit: Double,
    override val lat: Double,
    override val lng: Double
): ApartmentInfoDao