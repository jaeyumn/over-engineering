package noul.oe.domain.comment.infra

import noul.oe.domain.comment.repository.CommentRepository
import noul.oe.support.info.CommentInfoProvider
import noul.oe.support.info.dto.CommentInfo
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