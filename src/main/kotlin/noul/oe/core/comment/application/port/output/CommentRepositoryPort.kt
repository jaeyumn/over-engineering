package noul.oe.core.comment.application.port.output

import noul.oe.core.comment.domain.Comment

interface CommentRepositoryPort {
    fun save(comment: Comment)
    fun findById(id: Long): Comment
    fun findAllByParentId(parentId: Long): List<Comment>
    fun delete(commentId: Long)
    fun deleteAll(commentList: List<Comment>)
}