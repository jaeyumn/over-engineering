package noul.oe.core.comment.adapter.`in`.web

import jakarta.validation.constraints.NotBlank

data class CommentModifyRequest(
    @field:NotBlank
    val content: String,
)
