package noul.oe.core.post.application.port.input

data class LikeCommand(
    val postId: Long,
)

data class UnlikeCommand(
    val postId: Long,
)