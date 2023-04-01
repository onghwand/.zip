package com.uplus.zip.domain.maps.application.impl

import com.uplus.zip.domain.maps.application.ApartmentService
import com.uplus.zip.domain.maps.dao.ApartmentInfoDao
import com.uplus.zip.domain.maps.dao.ContractRepository
import com.uplus.zip.domain.maps.dao.DongRepository
import com.uplus.zip.domain.maps.dao.GuRepository
import com.uplus.zip.domain.maps.domain.ContractType
import com.uplus.zip.domain.maps.dto.MapInfoResponseDto
import com.uplus.zip.domain.maps.dto.MapRequestParameterDto
import com.uplus.zip.domain.maps.exception.MapErrorCode
import com.uplus.zip.global.error.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional(readOnly = true)
@Service
class ApartmentServiceImpl(
    private val contractRepository: ContractRepository,
    private val guRepository: GuRepository,
    private val dongRepository: DongRepository
) : ApartmentService {

    /**
     * zoom(Int)
     *  ~ 10.5 : 시 단위로 클러스터링된 정보 반환
     * 10.5 ~ 12 : 구 단위로 클러스터링된 정보 반환
     * 12 ~ 14 : 동 단위로 클러스터링된 정보 반환
     * 14 ~  : 아파트 개별 정보
     */
    override fun getInfoList(mapRequestParameterDto: MapRequestParameterDto): MapInfoResponseDto {
        val type = getContractType(mapRequestParameterDto.type)
        return when (mapRequestParameterDto.zoom) {
            in 0.0 .. 10.5 -> getSiInfoList(mapRequestParameterDto, type)
            in 10.5..12.0 -> getGuInfoList(mapRequestParameterDto, type)
            in 12.0..14.0 -> getDongInfoList(mapRequestParameterDto, type)
            else -> getApartmentInfoList(mapRequestParameterDto, type)
        }.let { MapInfoResponseDto(it) }
    }

    /**
     * 서울특별시에 존재하는 거래내역을 필터링하여 평균 가격을 계산한 정보 반환
     */
    protected fun getSiInfoList(
        mapRequestParameterDto: MapRequestParameterDto,
        type: ContractType
    ): List<ApartmentInfoDao> {
        return contractRepository.findSiList(
            mapRequestParameterDto.minYear, mapRequestParameterDto.maxYear,
            mapRequestParameterDto.minArea, mapRequestParameterDto.maxArea,
            mapRequestParameterDto.minPrice, mapRequestParameterDto.maxPrice,
            type, LocalDate.now().minusMonths(1L), LocalDate.now()
        )
    }

    /**
     * 앱 화면에 보이는 '구'별 존재하는 거래내역을 필터링하여 평균 가격을 계산한 정보 반환
     */
    protected fun getGuInfoList(
        mapRequestParameterDto: MapRequestParameterDto,
        type: ContractType
    ): List<ApartmentInfoDao> {
        val guIds = guRepository.findByLatBetweenAndLngBetween(
            mapRequestParameterDto.latSW, mapRequestParameterDto.latNE,
            mapRequestParameterDto.lngSW, mapRequestParameterDto.lngNE).map { it.guId }.toList()

        return contractRepository.findGuList(
            guIds,
            mapRequestParameterDto.minYear, mapRequestParameterDto.maxYear,
            mapRequestParameterDto.minArea, mapRequestParameterDto.maxArea,
            mapRequestParameterDto.minPrice, mapRequestParameterDto.maxPrice,
            type,
            LocalDate.now().minusMonths(1L), LocalDate.now()
        )
    }

    /**
     * 앱 화면에 보이는 '동'별 존재하는 거래내역을 필터링하여 평균 가격을 계산한 정보 반환
     */
    protected fun getDongInfoList(
        mapRequestParameterDto: MapRequestParameterDto,
        type: ContractType
    ): List<ApartmentInfoDao> {
        val dongIds = dongRepository.findByLatBetweenAndLngBetween(
                mapRequestParameterDto.latSW, mapRequestParameterDto.latNE,
                mapRequestParameterDto.lngSW, mapRequestParameterDto.lngNE).map { it.dongId }.toList()

        return contractRepository.findDongList(
            dongIds,
            mapRequestParameterDto.minYear, mapRequestParameterDto.maxYear,
            mapRequestParameterDto.minArea, mapRequestParameterDto.maxArea,
            mapRequestParameterDto.minPrice, mapRequestParameterDto.maxPrice,
            type, LocalDate.now().minusMonths(1L), LocalDate.now()
        )
    }

    /**
     * 앱 화면에 보이는 '아파트'별 거래내역을 필터링하여 평균 가격을 계산한 정보 반환
     */
    protected fun getApartmentInfoList(
        mapRequestParameterDto: MapRequestParameterDto,
        type: ContractType
    ): List<ApartmentInfoDao> {
        return contractRepository.findApartmentList(
            mapRequestParameterDto.latSW, mapRequestParameterDto.latNE,
            mapRequestParameterDto.lngSW, mapRequestParameterDto.lngNE,
            mapRequestParameterDto.minYear, mapRequestParameterDto.maxYear,
            mapRequestParameterDto.minArea, mapRequestParameterDto.maxArea,
            mapRequestParameterDto.minPrice, mapRequestParameterDto.maxPrice,
            type.name, LocalDate.now().minusMonths(1L), LocalDate.now()
        )
    }

    fun getContractType(type: String): ContractType = when (type) {
        "SALE" -> ContractType.SALE
        "LEASE" -> ContractType.LEASE
        "RENT" -> ContractType.RENT
        else -> throw CustomException(MapErrorCode.INVALID_CONTRACT_TYPE)
    }

}