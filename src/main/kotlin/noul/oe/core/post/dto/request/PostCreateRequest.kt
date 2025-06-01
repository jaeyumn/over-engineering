package noul.oe.core.post.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import noul.oe.core.post.entity.Post

data class PostCreateRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 255)
    val title: String,

    @field:NotBlank
    val content: String,
) {
    fun toEntity(userId: String): Post = Post(
        title = title,
        content = content,
        userId = userId,
    )
}
