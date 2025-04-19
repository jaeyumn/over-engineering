package noul.oe.comment.dto.request

import jakarta.validation.constraints.NotBlank

data class CommentCreateRequest(
    @field:NotBlank
    val content: String,
)