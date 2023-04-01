package com.uplus.zip.domain.review.application

import com.uplus.zip.domain.review.dto.ReviewCreateRequestDto
import com.uplus.zip.domain.review.dto.ReviewImageResponseDto
import com.uplus.zip.domain.review.dto.ReviewInfoResponseDto
import com.uplus.zip.global.common.dto.PaginationResponseDto

interface ReviewService {
    fun createReview(
        reviewCreateRequestDto: ReviewCreateRequestDto,
        apartmentId: Long
    )

    fun getReview(
        reviewId: Long
    ): ReviewInfoResponseDto?

    fun getReviews(
        apartmentId: Long, startId: Long, size: Int,
    ): PaginationResponseDto<ReviewInfoResponseDto>

    fun deleteReview(
        reviewId: Long
    )

    fun changeReviewLike(
        reviewId: Long
    )

    fun getReviewsAndImages(
        apartmentId: Long,
        startId: Long,
        size: Int
    ): PaginationResponseDto<ReviewImageResponseDto>

}