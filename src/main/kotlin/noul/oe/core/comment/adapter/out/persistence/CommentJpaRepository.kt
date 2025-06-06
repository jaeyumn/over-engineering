package noul.oe.core.comment.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface CommentJpaRepository : JpaRepository<CommentJpaEntity, Long> {
    fun findAllByPostId(postId: Long): List<CommentJpaEntity>
    fun findAllByParentId(parentId: Long): List<CommentJpaEntity>

    fun countByPostId(postId: Long): Int

    fun deleteAllByPostId(postId: Long)
}