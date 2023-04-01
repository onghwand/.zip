package com.uplus.zip.domain.comment.api

import com.uplus.zip.domain.comment.application.CommentService
import com.uplus.zip.domain.comment.dao.CommentInfo
import com.uplus.zip.domain.comment.dao.MockCommentInfo
import com.uplus.zip.domain.comment.dto.request.CommentCreateRequestDto
import com.uplus.zip.global.common.dto.PaginationMeta
import com.uplus.zip.global.common.dto.PaginationResponseDto
import io.mockk.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(CommentController::class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var commentService: CommentService

    @Test
    fun `댓글 생성 성공`() {
        val reviewId = 1L
        val commentCreateRequestDto = CommentCreateRequestDto("test", null)
        doNothing().`when`(commentService).createComment(reviewId, commentCreateRequestDto)

        mockMvc.perform(
            multipart("/reviews/${reviewId}/comments")
                .file(MockMultipartFile("image", null))
                .param("content", "test")
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `content가 공백인 댓글 생성 실패`() {
        val reviewId = 1L

        mockMvc.perform(
            multipart("/reviews/${reviewId}/comments")
                .file(MockMultipartFile("image", null))
                .param("content", "   ")
        )
            .andExpect(status().is4xxClientError)
            .andExpect(jsonPath("$.message").value("내용을 입력하세요."))
    }


    @Test
    fun `댓글 삭제 성공`() {
        val reviewId = 1L
        val commentId = 1L
        doNothing().`when`(commentService).deleteComment(commentId)

        mockMvc.perform(
            delete("/reviews/${reviewId}/comments/${commentId}")
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `댓글 좋아요 성공`() {
        val reviewId = 1L
        val commentId = 1L
        doNothing().`when`(commentService).likeComment(commentId)

        mockMvc.perform(
            delete("/reviews/${reviewId}/comments/${commentId}")
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `댓글 조회 성공`() {
        val reviewId = 1L
        val startId = 20L
        val size = 20

        val page = List(3) { MockCommentInfo() as CommentInfo }
        val meta = PaginationMeta(true, 3)

        `when`(commentService.getComments(reviewId, startId, size)).thenReturn(PaginationResponseDto(page, meta))

        mockMvc.perform(
            get("/reviews/${reviewId}/comments?start=${startId}&size=${size}")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.meta.hasNext").value(meta.hasNext))
            .andExpect(jsonPath("$.meta.total").value(meta.total))
            .andExpect(jsonPath("$.page.length()").value(page.size))
    }
}