package noul.oe.post.repository

import noul.oe.post.entity.PostLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostLikeRepository : JpaRepository<PostLike, Long> {
    fun existsByUserIdAndPostId(userId: String, postId: Long): Boolean
    fun findByUserIdAndPostId(userId: String, postId: Long): PostLike?
    fun deleteAllByPostId(postId: Long)
}