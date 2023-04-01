package com.uplus.zip.domain.comment.api

import com.uplus.zip.domain.comment.application.CommentService
import com.uplus.zip.domain.comment.dto.request.CommentCreateRequestDto
import com.uplus.zip.domain.comment.dao.CommentInfo
import com.uplus.zip.global.common.dto.PaginationResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/reviews/{reviewId}/comments")
class CommentController(private val commentService: CommentService) {

    @PostMapping
    fun createComment(
        @PathVariable("reviewId") reviewId: Long,
        @Valid @ModelAttribute commentCreateRequestDto: CommentCreateRequestDto
    ): ResponseEntity<Unit> {
        commentService.createComment(reviewId, commentCreateRequestDto)
        return ResponseEntity<Unit>(HttpStatus.CREATED)
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable("reviewId") reviewId: Long,
        @PathVariable("commentId") commentId: Long
    ): ResponseEntity<Unit> {
        commentService.deleteComment(commentId)
        return ResponseEntity<Unit>(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/{commentId}")
    fun likeComment(
        @PathVariable("reviewId") reviewId: Long,
        @PathVariable("commentId") commentId: Long
    ): ResponseEntity<Unit> {
        commentService.likeComment(commentId)
        return ResponseEntity<Unit>(HttpStatus.OK)
    }

    @GetMapping
    fun getComments(
        @PathVariable("reviewId") reviewId: Long,
        @RequestParam(
            name = "start",
            required = false,
            defaultValue = "9223372036854775807"
        ) startId: Long, // 페이지 시작 댓글Id
        @RequestParam(name = "size", required = false, defaultValue = "20") size: Int // 한 페이지 당 댓글수
    ): ResponseEntity<PaginationResponseDto<CommentInfo>> {
        return ResponseEntity<PaginationResponseDto<CommentInfo>>(
            commentService.getComments(reviewId, startId, size),
            HttpStatus.OK
        )
    }
}