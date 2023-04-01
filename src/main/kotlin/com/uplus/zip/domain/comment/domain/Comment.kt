package com.uplus.zip.domain.comment.domain

import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.review.domain.Review
import com.uplus.zip.global.common.domain.BaseTimeEntity
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
class Comment(
    @Column(nullable = false)
    val content: String,

    @Column(nullable = true)
    val imageThumbnailUrl: String?,

    @Column(nullable = true)
    val imageOriginUrl: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "review_id", nullable = false)
    val review: Review,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val commentId: Long? = null
) : BaseTimeEntity()
