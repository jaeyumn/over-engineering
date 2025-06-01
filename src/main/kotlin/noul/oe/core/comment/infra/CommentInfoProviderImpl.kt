package noul.oe.core.comment.infra

import noul.oe.core.comment.repository.CommentRepository
import noul.oe.support.port.CommentInfoProvider
import noul.oe.support.port.dto.CommentInfo
import org.springframework.stereotype.Component

@Component
class CommentInfoProviderImpl(
    private val commentRepository: CommentRepository,
) : CommentInfoProvider {

    override fun getCommentCount(postId: Long): Int {
        return commentRepository.countByPostId(postId)
    }

    override fun getCommentList(postId: Long, userId: String): List<CommentInfo> {
        return commentRepository.findAllByPostId(postId)
            .map {
                CommentInfo(
                    id = it.id,
                    content = it.content,
                    userId = it.userId,
                    createdAt = it.createdAt.toString(),
                    editable = it.userId == userId,
                )
            }
    }
}