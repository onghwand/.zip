package com.uplus.zip.domain.review.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.uplus.zip.domain.comment.domain.Comment
import com.uplus.zip.domain.maps.domain.Apartment
import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.global.common.domain.BaseTimeEntity
import javax.persistence.*

@Entity
class Review(
    @Column(nullable = false)
    val content: String,

    val imageOriginUrl: String?,
    
    val imageThumbnailUrl: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false)
    val apartment: Apartment,

    @OneToMany(mappedBy = "review", cascade = [CascadeType.ALL])
    val reviewLikes: MutableList<ReviewLikes> = mutableListOf(),

    @OneToMany(mappedBy = "review", cascade = [CascadeType.ALL])
    val comments: MutableList<Comment> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val reviewId: Long? = null

) : BaseTimeEntity()