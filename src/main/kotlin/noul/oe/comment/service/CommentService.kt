package noul.oe.comment.service

import noul.oe.comment.dto.request.CommentCreateRequest
import noul.oe.comment.dto.response.CommentResponse
import noul.oe.comment.entity.Comment
import noul.oe.comment.exception.CommentNotFoundException
import noul.oe.comment.exception.UnauthorizedCommentAccessException
import noul.oe.comment.repository.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentRepository: CommentRepository
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

    fun readAll(postId: Long): List<CommentResponse> {
        val comments = commentRepository.findAllByPostId(postId)
        val groupedComments = comments.groupBy { it.parentId }
        val rootComments = groupedComments[null].orEmpty()

        return rootComments.map { toResponseWithChildren(it, groupedComments) }
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
        groupedComments: Map<Long?, List<Comment>>
    ): CommentResponse {
        val children = groupedComments[comment.id]
            .orEmpty()
            .map { toResponseWithChildren(it, groupedComments) }

        return CommentResponse.from(comment, children)
    }
}