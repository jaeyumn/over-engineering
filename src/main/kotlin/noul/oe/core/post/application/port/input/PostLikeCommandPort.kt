package noul.oe.core.post.application.port.input

interface PostLikeCommandPort {
    fun like(postId: Long)
    fun unlike(postId: Long)
}