package com.uplus.zip.domain.maps.dao

import com.uplus.zip.domain.maps.domain.Apartment
import com.uplus.zip.domain.search.dao.AutoComplete
import com.uplus.zip.domain.search.dao.SearchResult
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ApartmentRepository : JpaRepository<Apartment, Long> {
    @Query(
        "select concat(d.name,' ',a.apartmentName) as name, " +
                "a.lat as lat, " +
                "a.lng as lng " +
                "from Apartment a join Dong d on a.dong.dongId = d.dongId " +
                "where concat(d.name,' ',a.apartmentName) like %:keyword% "
    )
    fun find7AutoCompletesByKeyword(keyword: String, pageRequest: PageRequest): List<AutoComplete>

    @Query(
        "select a.apartmentId as id, " +
                "a.apartmentName as name, " +
                "concat(a.si,' ',a.gu.name,' ',d.name,' ',a.bunzi) as address, " +
                "a.builtYear as yearBuilt, " +
                "a.lat as lat, " +
                "a.lng as lng " +
                "from Apartment a join Dong d on a.dong.dongId = d.dongId " +
                "where concat(d.name,' ',a.apartmentName) like %:keyword% "
    )
    fun find10SearchResponseByKeyword(keyword: String, pageRequest: PageRequest): List<SearchResult>
}