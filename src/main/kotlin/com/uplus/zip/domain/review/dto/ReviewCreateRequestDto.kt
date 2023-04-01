package com.uplus.zip.domain.review.dto

import com.uplus.zip.domain.maps.domain.Apartment
import com.uplus.zip.domain.member.domain.Member
import com.uplus.zip.domain.review.domain.Review
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank

data class ReviewCreateRequestDto(
    @field:NotBlank(message = "글 내용이 없습니다.")
    val content: String,
    val image: MultipartFile?
) {
    fun toEntity(member: Member, apartment: Apartment, imageOriginUrl: String?, imageThumbnailUrl: String?) =
        Review(
            content = content,
            imageOriginUrl = imageOriginUrl,
            member = member,
            apartment = apartment,
            imageThumbnailUrl = imageThumbnailUrl
        )
}