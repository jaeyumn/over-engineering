package noul.oe.core.post.application.port.input

import noul.oe.core.post.domain.Post

data class CreateCommand(
    val title: String,
    val content: String,
) {
    fun toDomain(userId: String): Post =
        Post(userId = userId, title = title, content = content)
}

data class ModifyCommand(
    val postId: Long,
    val title: String,
    val content: String,
)

data class RemoveCommand(
    val postId: Long,
)