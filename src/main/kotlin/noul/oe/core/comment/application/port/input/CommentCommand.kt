package noul.oe.core.comment.application.port.input

import noul.oe.core.comment.domain.Comment

data class CreateCommentCommand(
    val postId: Long,
    val content: String,
    val userId: String,
) {
    fun toDomain(): Comment =
        Comment(
            content = content,
            postId = postId,
            userId = userId,
        )
}

data class ReplyCommentCommand(
    val parentId: Long,
    val content: String,
)

data class ModifyCommentCommand(
    val commentId: Long,
    val content: String,
)
