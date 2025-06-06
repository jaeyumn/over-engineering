package noul.oe.core.post.application.port.input

interface PostCommandPort {
    fun create(command: CreatePostCommand)
    fun modify(command: ModifyPostCommand)
    fun remove(postId: Long)
}