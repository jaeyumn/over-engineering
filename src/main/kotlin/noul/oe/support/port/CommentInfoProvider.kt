package noul.oe.support.port

import noul.oe.support.port.dto.CommentInfo

interface CommentInfoProvider {
    fun getCommentCount(postId: Long): Int
    fun getCommentList(postId: Long, userId: String): List<CommentInfo>
    fun deleteAllComment(postId: Long)
}