package com.uplus.zip.domain.comment.dao

import com.uplus.zip.domain.comment.domain.Comment
import com.uplus.zip.domain.comment.domain.CommentLikes
import com.uplus.zip.domain.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikesRepository : JpaRepository<CommentLikes, Long> {
    fun findByMemberAndComment(member: Member, comment: Comment): CommentLikes?
}