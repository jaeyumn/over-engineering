package noul.oe.core.post.adapter.out

import org.springframework.data.jpa.repository.JpaRepository

interface PostLikeJpaRepository : JpaRepository<PostLikeJpaEntity, Long> {
    fun existsByUserIdAndPostId(userId: String, postId: Long): Boolean
    fun findByUserIdAndPostId(userId: String, postId: Long): PostLikeJpaEntity?
    fun deleteAllByPostId(postId: Long)
}