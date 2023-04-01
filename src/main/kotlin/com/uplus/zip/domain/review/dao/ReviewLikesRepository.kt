package com.uplus.zip.domain.review.dao

import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.review.domain.Review
import com.uplus.zip.domain.review.domain.ReviewLikes
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewLikesRepository : JpaRepository<ReviewLikes, Long> {
    fun findByReviewAndMember(review: Review, member: Member): ReviewLikes?
    fun countByReview(review: Review): Int
}