package noul.oe.core.comment.application.port.input

import java.time.LocalDateTime

data class CommentDetailResponse(
    val commentId: Long,
    val content: String,
    val userId: String,
    val username: String,
    val editable: Boolean,
    val createdAt: LocalDateTime,
    val children: List<CommentDetailResponse>? = emptyList()
)