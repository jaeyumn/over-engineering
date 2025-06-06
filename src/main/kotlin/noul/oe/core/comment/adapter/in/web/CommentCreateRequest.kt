package noul.oe.core.comment.adapter.`in`.web

import jakarta.validation.constraints.NotBlank

data class CommentCreateRequest(
    @field:NotBlank
    val content: String,
)