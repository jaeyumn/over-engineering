package noul.oe.core.comment.dto.request

import jakarta.validation.constraints.NotBlank

data class CommentModifyRequest(
    @field:NotBlank
    val content: String,
)
