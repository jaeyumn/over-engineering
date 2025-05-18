package noul.oe.domain.comment.service

import noul.oe.domain.comment.dto.request.CommentCreateRequest
import noul.oe.domain.comment.dto.response.CommentResponse
import noul.oe.domain.comment.entity.Comment
import noul.oe.domain.comment.exception.CommentNotFoundException
import noul.oe.domain.comment.exception.UnauthorizedCommentAccessException
import noul.oe.domain.comment.repository.CommentRepository
import noul.oe.domain.user.entity.User
import noul.oe.domain.user.exception.UserNotFoundException
import noul.oe.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(postId: Long, userId: String, request: CommentCreateRequest): CommentResponse {
        val comment = Comment(
            content = request.content,
            postId = postId,
            userId = userId,
        )
        val savedComment = commentRepository.save(comment)

        return CommentResponse.from(savedComment)
    }

    @Transactional
    fun reply(parentId: Long, userId: String, request: CommentCreateRequest): CommentResponse {
        val parentComment = getComment(parentId)
        val reply = Comment(
            content = request.content,
            postId = parentComment.postId,
            userId = userId,
            parentId = parentId
        )
        val savedReply = commentRepository.save(reply)

        return CommentResponse.from(savedReply)
    }

    fun readAll(postId: Long, currentUserId: String): List<CommentResponse> {
        val comments = commentRepository.findAllByPostId(postId)
        val userIds = comments.map { it.userId }.toSet()
        val users = userRepository.findAllById(userIds).associateBy { it.id }

        val groupedComments = comments.groupBy { it.parentId }
        val rootComments = groupedComments[null].orEmpty()

        return rootComments.map { toResponseWithChildren(it, groupedComments, users, currentUserId) }
    }

    @Transactional
    fun modify(commentId: Long, userId: String, newContent: String) {
        val comment = getComment(commentId)
        if (comment.isNotOwnedBy(userId)) {
            throw UnauthorizedCommentAccessException("This comment is not owned by: userId=$userId")
        }
        comment.modify(newContent)
    }

    @Transactional
    fun remove(commentId: Long, userId: String) {
        val comment = getComment(commentId)
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
        users: Map<String, User>,
        currentUserId: String
    ): CommentResponse {
        val children = groupedComments[comment.id]
            .orEmpty()
            .map { toResponseWithChildren(it, groupedComments, users, currentUserId) }
        val user = users[comment.userId] ?: throw UserNotFoundException("User not found: userId=${comment.userId}")
        return CommentResponse.from(comment, user, currentUserId, children)
    }

    private fun getComment(commentId: Long): Comment {
        return commentRepository.findById(commentId)
            .orElseThrow { CommentNotFoundException("Comment not found: commentId=$commentId") }
    }
}