package noul.oe.core.comment.adapter.out.info

import noul.oe.core.comment.adapter.out.persistence.CommentJpaRepository
import noul.oe.support.port.CommentInfoProvider
import noul.oe.support.port.dto.CommentInfo
import org.springframework.stereotype.Component

@Component
class CommentInfoProviderImpl(
    private val commentRepository: CommentJpaRepository,
) : CommentInfoProvider {

    override fun getCommentCount(postId: Long): Int {
        return commentRepository.countByPostId(postId)
    }

    override fun getCommentList(postId: Long, userId: String): List<CommentInfo> {
        val commentList = commentRepository.findAllByPostId(postId)
        val commentInfoMap = commentList.associate { comment ->
            comment.id to CommentInfo(
                id = comment.id,
                content = comment.content,
                userId = comment.userId,
                username = "",
                createdAt = comment.createdAt.toString(),
                editable = comment.userId == userId,
                children = emptyList()
            )
        }.toMutableMap()

        val rootCommentList = mutableListOf<CommentInfo>()
        commentList.forEach { comment ->
            val commentInfo = commentInfoMap[comment.id]!!
            if (comment.parentId == null) {
                rootCommentList.add(commentInfo)

            } else {
                val parentInfo = commentInfoMap[comment.parentId]!!
                val updatedChildren = parentInfo.children.toMutableList()
                updatedChildren.add(commentInfo)
                commentInfoMap[comment.parentId!!] = parentInfo.copy(children = updatedChildren)
            }
        }

        return rootCommentList
    }

    override fun deleteAllComment(postId: Long) {
        commentRepository.deleteAllByPostId(postId)
    }
}