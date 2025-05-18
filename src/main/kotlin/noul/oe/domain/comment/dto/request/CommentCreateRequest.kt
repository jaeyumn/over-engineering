package noul.oe.domain.comment.dto.request

import jakarta.validation.constraints.NotBlank

data class CommentCreateRequest(
    @field:NotBlank
    val content: String,
)