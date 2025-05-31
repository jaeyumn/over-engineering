package noul.oe.support.info

import noul.oe.support.info.dto.CommentInfo

interface CommentInfoProvider {
    fun getCommentCount(postId: Long): Int
    fun getCommentList(postId: Long, userId: String): List<CommentInfo>
}