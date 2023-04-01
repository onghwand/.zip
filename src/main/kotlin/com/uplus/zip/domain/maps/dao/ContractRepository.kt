package com.uplus.zip.domain.maps.dao

import com.uplus.zip.domain.maps.domain.Contract
import com.uplus.zip.domain.maps.domain.ContractType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ContractRepository : JpaRepository<Contract, Long> {

    @Query(
        "SELECT a.apartment_id AS id, a.apartment_name AS title," +
                "CASE WHEN c.price IS NULL THEN 0 WHEN a.built_year BETWEEN :minYear AND :maxYear THEN c.price ELSE 0 END AS price, " +
                "CASE WHEN c.deposit IS NULL THEN 0 WHEN a.built_year BETWEEN :minYear AND :maxYear THEN c.deposit ELSE 0 END AS deposit, " +
                "a.lat AS lat, a.lng AS lng " +
                "FROM (SELECT apartment_id, avg(price) AS price, avg(deposit) AS deposit " +
                    "FROM contract " +
                    "WHERE contract_date BETWEEN :startDate AND :endDate AND type = :type AND price BETWEEN :minPrice AND :maxPrice AND area BETWEEN :minArea AND :maxArea " +
                    "GROUP BY apartment_id) AS c " +
                "RIGHT OUTER JOIN apartment a ON c.apartment_id = a.apartment_id " +
                "WHERE a.lat BETWEEN :minLat AND :maxLat AND a.lng BETWEEN :minLng AND :maxLng"
    , nativeQuery = true)
    fun findApartmentList(
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLng") minLng: Double,
        @Param("maxLng") maxLng: Double,
        @Param("minYear") minYear: Int,
        @Param("maxYear") maxYear: Int,
        @Param("minArea") minArea: Double,
        @Param("maxArea") maxArea: Double,
        @Param("minPrice") minPrice: Int,
        @Param("maxPrice") maxPrice: Int,
        @Param("type") type: String,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): MutableList<ApartmentInfoDao>

    @Query(
        "SELECT 9999999L AS id, a.si AS title, AVG(c.price) AS price, AVG(c.deposit) AS deposit, 37.5642135 AS lat, 127.0016985 AS lng " +
                "FROM Contract c JOIN c.apartment a " +
                "WHERE c.contractDate BETWEEN :startDate AND :endDate " +
                "AND c.type = :type " +
                "AND a.builtYear BETWEEN :minYear AND :maxYear AND c.area BETWEEN :minArea AND :maxArea " +
                "AND c.price BETWEEN :minPrice AND :maxPrice " +
                "GROUP BY a.si"
    )
    fun findSiList(
        @Param("minYear") minYear: Int,
        @Param("maxYear") maxYear: Int,
        @Param("minArea") minArea: Double,
        @Param("maxArea") maxArea: Double,
        @Param("minPrice") minPrice: Int,
        @Param("maxPrice") maxPrice: Int,
        @Param("type") type: ContractType,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): MutableList<ApartmentInfoDao>



    @Query(
        "SELECT g.guId AS id, g.name AS title, AVG(c.price) AS price, AVG(c.deposit) AS deposit, g.lat AS lat, g.lng AS lng " +
                "FROM Contract c JOIN c.apartment a JOIN a.gu g " +
                "WHERE g.guId IN :guIds AND c.contractDate BETWEEN :startDate AND :endDate " +
                "AND c.type = :type " +
                "AND a.builtYear BETWEEN :minYear AND :maxYear AND c.area BETWEEN :minArea AND :maxArea " +
                "AND c.price BETWEEN :minPrice AND :maxPrice " +
                "GROUP BY g.guId"
    )
    fun findGuList(
        @Param("guIds") guIds: List<Long?>,
        @Param("minYear") minYear: Int,
        @Param("maxYear") maxYear: Int,
        @Param("minArea") minArea: Double,
        @Param("maxArea") maxArea: Double,
        @Param("minPrice") minPrice: Int,
        @Param("maxPrice") maxPrice: Int,
        @Param("type") type: ContractType,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): MutableList<ApartmentInfoDao>

    @Query(
        "SELECT d.dongId AS id, d.name AS title, AVG(c.price) AS price, AVG(c.deposit) AS deposit, d.lat AS lat, d.lng AS lng " +
                "FROM Contract c JOIN c.apartment a JOIN a.dong d " +
                "WHERE d.dongId in :dongIds AND c.contractDate BETWEEN :startDate AND :endDate " +
                "AND c.type = :type " +
                "AND a.builtYear BETWEEN :minYear AND :maxYear AND c.area BETWEEN :minArea AND :maxArea " +
                "AND c.price BETWEEN :minPrice AND :maxPrice " +
                "GROUP BY d.dongId "
    )
    fun findDongList(
        @Param("dongIds") dongIds: List<Long?>,
        @Param("minYear") minYear: Int,
        @Param("maxYear") maxYear: Int,
        @Param("minArea") minArea: Double,
        @Param("maxArea") maxArea: Double,
        @Param("minPrice") minPrice: Int,
        @Param("maxPrice") maxPrice: Int,
        @Param("type") type: ContractType,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): MutableList<ApartmentInfoDao>

}