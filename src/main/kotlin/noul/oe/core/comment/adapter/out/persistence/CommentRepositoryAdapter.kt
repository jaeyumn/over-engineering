package noul.oe.core.comment.adapter.out.persistence

import noul.oe.core.comment.application.exception.CommentNotFoundException
import noul.oe.core.comment.application.port.output.CommentRepositoryPort
import noul.oe.core.comment.domain.Comment
import org.springframework.stereotype.Repository

@Repository
class CommentRepositoryAdapter(
    private val commentJpaRepository: CommentJpaRepository
) : CommentRepositoryPort {

    override fun save(comment: Comment) {
        val entity = CommentMapper.toEntity(comment)
        commentJpaRepository.save(entity)
    }

    override fun findById(id: Long): Comment {
        val entity = commentJpaRepository.findById(id)
            .orElseThrow { CommentNotFoundException(id) }
        return CommentMapper.toDomain(entity)
    }

    override fun findAllByParentId(parentId: Long): List<Comment> {
        return commentJpaRepository.findAllByParentId(parentId)
            .map(CommentMapper::toDomain)
    }

    override fun delete(commentId: Long) {
        commentJpaRepository.deleteById(commentId)
    }

    override fun deleteAll(commentList: List<Comment>) {
        val ids = commentList.map { it.id }
        commentJpaRepository.deleteAllById(ids)
    }
}