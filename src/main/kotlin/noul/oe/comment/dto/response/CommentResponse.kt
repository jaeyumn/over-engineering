package noul.oe.comment.dto.response

import noul.oe.comment.entity.Comment
import noul.oe.user.entity.User
import java.time.LocalDateTime

data class CommentResponse(
    val id: Long,
    val content: String,
    val userId: String,
    val username: String,
    val editable: Boolean,
    val createdAt: LocalDateTime,
    val children: List<CommentResponse> = emptyList(),
) {
    companion object {
        fun from(comment: Comment): CommentResponse {
            return CommentResponse(
                id = comment.id,
                content = comment.content,
                userId = comment.userId,
                username = "TODO", // TODO
                editable = true, // TODO
                createdAt = comment.createdAt,
                children = emptyList(),
            )
        }

        fun from(comment: Comment, user: User, currentUserId: String, children: List<CommentResponse> = emptyList()): CommentResponse {
            return CommentResponse(
                id = comment.id,
                content = comment.content,
                userId = comment.userId,
                username = user.username,
                editable = comment.userId == currentUserId,
                createdAt = comment.createdAt,
                children = children,
            )
        }
    }
}