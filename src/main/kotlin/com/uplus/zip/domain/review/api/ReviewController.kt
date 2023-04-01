package com.uplus.zip.domain.review.api

import com.uplus.zip.domain.review.application.ReviewService
import com.uplus.zip.domain.review.dto.ReviewCreateRequestDto
import com.uplus.zip.domain.review.dto.ReviewImageResponseDto
import com.uplus.zip.domain.review.dto.ReviewInfoResponseDto
import com.uplus.zip.global.common.dto.PaginationResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/reviews")
class ReviewController(private val reviewService: ReviewService) {

    @GetMapping("/{apartmentId}")
    fun getReviews(
        @PathVariable apartmentId: Long,
        @RequestParam(required = false, defaultValue = Long.MAX_VALUE.toString()) start: Long,
        @RequestParam(required = false, defaultValue = "20") size: Int,
    ): ResponseEntity<PaginationResponseDto<ReviewInfoResponseDto>> = ResponseEntity.ok(
        reviewService.getReviews(
            apartmentId = apartmentId,
            startId = start,
            size = size,
        )
    )

    @GetMapping("/{apartmentId}/detail/{reviewId}")
    fun getReview(
        @PathVariable apartmentId: Long,
        @PathVariable reviewId: Long,
    ): ResponseEntity<ReviewInfoResponseDto> {
        return ResponseEntity.ok(reviewService.getReview(reviewId))
    }

    @PostMapping("/{apartmentId}")
    fun addReview(
        @PathVariable apartmentId: Long,
        @Valid @ModelAttribute reviewCreateRequestDto: ReviewCreateRequestDto,
    ): ResponseEntity<Unit> {
        reviewService.createReview(reviewCreateRequestDto, apartmentId)
        return ResponseEntity<Unit>(HttpStatus.CREATED)
    }

    @DeleteMapping("/{apartmentId}/{reviewId}")
    fun deleteReview(
        @PathVariable apartmentId: Long,
        @PathVariable reviewId: Long
    ): ResponseEntity<Unit> {
        reviewService.deleteReview(reviewId)
        return ResponseEntity<Unit>(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/{apartmentId}/like/{reviewId}")
    fun likeReview(
        @PathVariable apartmentId: Long,
        @PathVariable reviewId: Long
    ): ResponseEntity<Unit> {
        reviewService.changeReviewLike(reviewId = reviewId)
        return ResponseEntity<Unit>(HttpStatus.OK)
    }

    @GetMapping("/{apartmentId}/images")
    fun getReviewsWithImages(
        @PathVariable apartmentId: Long,
        @RequestParam(required = false, defaultValue = Long.MAX_VALUE.toString()) start: Long,
        @RequestParam(required = false, defaultValue = "20") size: Int,
    ): ResponseEntity<PaginationResponseDto<ReviewImageResponseDto>> {
        return ResponseEntity.ok(reviewService.getReviewsAndImages(apartmentId, start, size))
    }
}