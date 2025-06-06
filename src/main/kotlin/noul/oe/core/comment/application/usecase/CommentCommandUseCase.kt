package noul.oe.core.comment.application.usecase

import noul.oe.core.comment.application.exception.UnauthorizedCommentAccessException
import noul.oe.core.comment.application.port.input.*
import noul.oe.core.comment.application.port.output.CommentRepositoryPort
import noul.oe.core.comment.domain.Comment
import noul.oe.support.security.SecurityUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentCommandUseCase(
    private val commentRepositoryPort: CommentRepositoryPort
) : CommentCommandPort {

    override fun create(command: CreateCommentCommand) {
        val comment = command.toDomain()
        commentRepositoryPort.save(comment)
    }

    override fun reply(command: ReplyCommentCommand) {
        val parentId = command.parentId
        val parentComment = commentRepositoryPort.findById(parentId)
        val comment = Comment(
            id = parentComment.id,
            content = command.content,
            postId = parentComment.postId,
            userId = parentComment.userId,
            parentId = parentId,
        )
        commentRepositoryPort.save(comment)
    }

    override fun modify(command: ModifyCommentCommand) {
        val comment = commentRepositoryPort.findById(command.commentId)
        verifyAuthor(comment)

        comment.modify(command.content)
        commentRepositoryPort.save(comment)
    }

    override fun remove(commentId: Long) {
        val comment = commentRepositoryPort.findById(commentId)
        verifyAuthor(comment)

        // 댓글과 관련된 답글까지 같이 삭제
        val replies = commentRepositoryPort.findAllByParentId(comment.id)
        commentRepositoryPort.deleteAll(replies)
        commentRepositoryPort.delete(commentId)
    }

    // 권한 체크
    private fun verifyAuthor(comment: Comment) {
        val userId = SecurityUtils.getCurrentUser().userId
        if (comment.isNotOwnedBy(userId)) {
            throw UnauthorizedCommentAccessException(userId)
        }
    }
}