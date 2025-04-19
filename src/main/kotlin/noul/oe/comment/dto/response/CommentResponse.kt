package noul.oe.comment.dto.response

import noul.oe.comment.entity.Comment
import java.time.LocalDateTime

data class CommentResponse(
    val id: Long,
    val content: String,
    val userId: String,
    val createdAt: LocalDateTime,
    val children: List<CommentResponse> = emptyList(),
) {
    companion object {
        fun from(comment: Comment, children: List<CommentResponse> = emptyList()): CommentResponse {
            return CommentResponse(
                id = comment.id,
                content = comment.content,
                userId = comment.userId,
                createdAt = comment.createdAt,
                children = children
            )
        }
    }
}