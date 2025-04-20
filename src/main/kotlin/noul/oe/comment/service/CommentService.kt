package noul.oe.comment.service

import noul.oe.comment.dto.request.CommentCreateRequest
import noul.oe.comment.dto.response.CommentResponse
import noul.oe.comment.entity.Comment
import noul.oe.comment.exception.CommentNotFoundException
import noul.oe.comment.exception.UnauthorizedCommentAccessException
import noul.oe.comment.repository.CommentRepository
import noul.oe.user.entity.User
import noul.oe.user.exception.UserNotFoundException
import noul.oe.user.repository.UserRepository
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
        val parentComment = commentRepository.findById(parentId)
            .orElseThrow { CommentNotFoundException() }

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
        val comment = commentRepository.findById(commentId)
            .orElseThrow { CommentNotFoundException() }

        if (comment.isNotOwnedBy(userId)) {
            throw UnauthorizedCommentAccessException()
        }

        comment.modify(newContent)
    }

    @Transactional
    fun remove(commentId: Long, userId: String) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { CommentNotFoundException() }

        if (comment.isNotOwnedBy(userId)) {
            throw UnauthorizedCommentAccessException()
        }

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
        val user = users[comment.userId] ?: throw UserNotFoundException()
        return CommentResponse.from(comment, user, currentUserId, children)
    }
}