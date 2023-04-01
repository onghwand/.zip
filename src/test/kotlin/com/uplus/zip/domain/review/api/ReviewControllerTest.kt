package com.uplus.zip.domain.review.api

import com.uplus.zip.domain.review.application.ReviewService
import com.uplus.zip.domain.review.dto.ReviewCreateRequestDto
import com.uplus.zip.domain.review.dto.ReviewInfoResponseDto
import com.uplus.zip.domain.review.exception.ReviewErrorCode
import com.uplus.zip.global.common.dto.PaginationMeta
import com.uplus.zip.global.common.dto.PaginationResponseDto
import com.uplus.zip.global.error.CustomException
import io.kotest.core.spec.style.BehaviorSpec
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(ReviewController::class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest : BehaviorSpec() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var reviewService: ReviewService
    @DisplayName("리뷰 생성 성공")
    @Test
    fun 사용자가_리뷰를_작성에_성공하면() {
        val reviewCreateRequestDto = ReviewCreateRequestDto(
            content = "리뷰 내용입니다.",
            image = null,
        )
        Mockito.doNothing().`when`(reviewService).createReview(reviewCreateRequestDto, 1)
        mockMvc.perform(
            multipart("/reviews/{apartmentId}", 1L)
                .file(MockMultipartFile("image", null))
                .param("content", "content")
        ).andExpect(status().isCreated)
    }

    @DisplayName("리뷰 조회 성공")
    @Test
    fun 사용자가_리뷰_조회에_성공하면() {
        val reviewInfoResponseDto =
            ReviewInfoResponseDto(1, LocalDateTime.now(), "test", null, null, 1, "test", 0, 0, 0)
        val result = PaginationResponseDto(listOf(reviewInfoResponseDto), PaginationMeta(false, 1))
        Mockito.`when`(reviewService.getReviews(1, Long.MAX_VALUE, 20)).thenReturn(result)
        mockMvc.perform(get("/reviews/{apartmentId}", 1L)).andExpect(status().isOk)
    }

    @DisplayName("리뷰 삭제 성공")
    @Test
    fun 사용자가_리뷰_삭제_성공하면() {
        Mockito.doNothing().`when`(reviewService).deleteReview(1)
        mockMvc.perform(delete("/reviews/{apartmentId}/{reviewId}", 1L, 1L)).andExpect(status().isNoContent)
    }

    @DisplayName("리뷰 삭제 실패 - 리뷰가 존재하지 않음")
    @Test
    fun 사용자가_존재하지_않는_리뷰_삭제() {
        Mockito.`when`(reviewService.deleteReview(1)).thenThrow(CustomException(ReviewErrorCode.REVIEW_NOT_FOUND))
        mockMvc.perform(delete("/reviews/{apartmentId}/{reviewId}", 1L, 1L)).andExpect(status().isNotFound)
    }

    @DisplayName("리뷰 삭제 실패 - 자신이 작성한 글이 아님")
    @Test
    fun 사용자가_자신이_작성하지_않은_리뷰_삭제() {
        Mockito.`when`(reviewService.deleteReview(1)).thenThrow(CustomException(ReviewErrorCode.REVIEW_FORBIDDEN))
        mockMvc.perform(delete("/reviews/{apartmentId}/{reviewId}", 1L, 1L)).andExpect(status().isForbidden)
    }

    @DisplayName("좋아요 성공")
    @Test
    fun 사용자가_리뷰에_좋아요() {
        Mockito.doNothing().`when`(reviewService).changeReviewLike(1)
        mockMvc.perform(post("/reviews/{apartmentId}/like/{reviewId}", 1L, 1L)).andExpect(status().isOk)
    }

    @DisplayName("좋아요 실패")
    @Test
    fun 사용자가_리뷰에_좋아요_실패() {
        Mockito.`when`(reviewService.changeReviewLike(1)).thenThrow(CustomException(ReviewErrorCode.REVIEW_NOT_FOUND))
        mockMvc.perform(post("/reviews/{apartmentId}/like/{reviewId}", 1L, 1L)).andExpect(status().isNotFound)
    }
}