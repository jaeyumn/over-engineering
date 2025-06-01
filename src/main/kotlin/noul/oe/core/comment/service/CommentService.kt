package noul.oe.core.comment.service

import noul.oe.core.comment.dto.request.CommentCreateRequest
import noul.oe.core.comment.dto.response.CommentResponse
import noul.oe.core.comment.entity.Comment
import noul.oe.core.comment.exception.CommentNotFoundException
import noul.oe.core.comment.exception.UnauthorizedCommentAccessException
import noul.oe.core.comment.repository.CommentRepository
import noul.oe.support.port.UserInfoProvider
import noul.oe.support.security.SecurityUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentRepository: CommentRepository,
    private val userInfoProvider: UserInfoProvider,
) {
    @Transactional
    fun create(postId: Long, request: CommentCreateRequest): CommentResponse {
        val comment = Comment(
            content = request.content,
            postId = postId,
            userId = SecurityUtils.getCurrentUser().userId,
        )
        val savedComment = commentRepository.save(comment)

        return CommentResponse.from(savedComment)
    }

    @Transactional
    fun reply(parentId: Long, request: CommentCreateRequest): CommentResponse {
        val parentComment = getComment(parentId)
        val reply = Comment(
            content = request.content,
            postId = parentComment.postId,
            userId = SecurityUtils.getCurrentUser().userId,
            parentId = parentId
        )
        val savedReply = commentRepository.save(reply)

        return CommentResponse.from(savedReply)
    }

    fun readAll(postId: Long, currentUserId: String): List<CommentResponse> {
        val comments = commentRepository.findAllByPostId(postId)
        val groupedComments = comments.groupBy { it.parentId }
        val rootComments = groupedComments[null].orEmpty()

        return rootComments.map { toResponseWithChildren(it, groupedComments, currentUserId) }
    }

    @Transactional
    fun modify(commentId: Long, newContent: String) {
        val comment = getComment(commentId)
        val userId = SecurityUtils.getCurrentUser().userId
        if (comment.isNotOwnedBy(userId)) {
            throw UnauthorizedCommentAccessException("This comment is not owned by: userId=$userId")
        }
        comment.modify(newContent)
    }

    @Transactional
    fun remove(commentId: Long) {
        val comment = getComment(commentId)
        val userId = SecurityUtils.getCurrentUser().userId
        if (comment.isNotOwnedBy(userId)) {
            throw UnauthorizedCommentAccessException("This comment is not owned by: userId=$userId")
        }

        // 댓글과 관련된 답글까지 같이 삭제
        val replies = commentRepository.findAllByParentId(commentId)
        commentRepository.deleteAll(replies)
        commentRepository.delete(comment)
    }

    private fun toResponseWithChildren(
        comment: Comment,
        groupedComments: Map<Long?, List<Comment>>,
        currentUserId: String
    ): CommentResponse {
        val children = groupedComments[comment.id]
            .orEmpty()
            .map { toResponseWithChildren(it, groupedComments, currentUserId) }
        val username = userInfoProvider.getUsername(comment.userId)
        return CommentResponse.from(comment, username, currentUserId, children)
    }

    private fun getComment(commentId: Long): Comment {
        return commentRepository.findById(commentId)
            .orElseThrow { CommentNotFoundException("Comment not found: commentId=$commentId") }
    }
}