package noul.oe.core.comment.application.port.input

interface CommentCommandPort {
    fun create(command: CreateCommentCommand)
    fun reply(command: ReplyCommentCommand)
    fun modify(command: ModifyCommentCommand)
    fun remove(commentId: Long)
}