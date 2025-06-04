package noul.oe.core.post.application.port.input

interface PostLikeCommandPort {
    fun like(command: LikeCommand)
    fun unlike(command: UnlikeCommand)
}