package noul.oe.domain.comment.repository

import noul.oe.domain.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByPostId(postId: Long): List<Comment>
    fun findAllByParentId(parentId: Long): List<Comment>

    fun countByPostId(postId: Long): Int
}