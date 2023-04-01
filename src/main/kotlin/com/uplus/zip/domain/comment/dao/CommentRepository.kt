package com.uplus.zip.domain.comment.dao

import com.uplus.zip.domain.comment.domain.Comment
import com.uplus.zip.domain.review.domain.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentRepository : JpaRepository<Comment, Long> {
    @Query(
        "select cm.commentId as id, " +
                "cm.member.nickname as nickname, " +
                "cm.member.memberId as memberId, " +
                "cm.content as content, " +
                "cm.createdAt as createdAt, " +
                "cm.imageOriginUrl as imageOriginUrl, " +
                "cm.imageThumbnailUrl as imageThumbnailUrl, " +
                "sum(case when cl.member != null then 1 else 0 end) as likesCount, " +
                "case when sum(case when cl.member.memberId =:memberId then 1 else 0 end) = 1 then true else false end as likedByCurrentUser " +
                "from Comment cm " +
                "left join CommentLikes cl on cm.commentId = cl.comment.commentId " +
                "where cm.commentId < :startId and cm.review.reviewId = :reviewId " +
                "group by cm.commentId, cm.member.nickname " +
                "order by cm.createdAt desc"
    )
    fun findAllByJpql(
        @Param("reviewId") reviewId: Long,
        @Param("memberId") memberId: Long?,
        @Param("startId") startId: Long,
        limit: PageRequest
    ): Page<CommentInfo>

    fun countByReview(review: Review): Int
}