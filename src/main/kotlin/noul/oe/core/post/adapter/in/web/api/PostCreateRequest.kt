package noul.oe.core.post.adapter.`in`.web.api

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import noul.oe.core.post.adapter.out.PostJpaEntity

data class PostCreateRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 255)
    val title: String,

    @field:NotBlank
    val content: String,
) {
    fun toEntity(userId: String): PostJpaEntity = PostJpaEntity(
        title = title,
        content = content,
        userId = userId,
    )
}
