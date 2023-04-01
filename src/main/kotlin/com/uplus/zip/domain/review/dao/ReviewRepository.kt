package com.uplus.zip.domain.review.dao

import com.uplus.zip.domain.maps.domain.Apartment
import com.uplus.zip.domain.review.domain.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Long> {

    fun findByApartmentAndReviewIdLessThanOrderByCreatedAtDesc(
        apartment: Apartment, reviewId: Long, pageable: Pageable
    ): Page<Review>

    fun findByApartmentAndReviewIdLessThanAndImageOriginUrlNotNullOrderByCreatedAtDesc(
        apartment: Apartment,
        reviewId: Long,
        pageable: Pageable
    ): Page<ImageDao>

    fun countByApartmentAndImageOriginUrlNotNullOrderByCreatedAtDesc(
        apartment: Apartment
    ): Long

    fun countByApartment(
        apartment: Apartment
    ): Long
}