package noul.oe.core.post.application.port.output

import noul.oe.core.post.domain.PostLike

interface PostLikeRepositoryPort {
    fun save(postLike: PostLike)

    fun findByUserIdAndPostId(userId: String, postId: Long): PostLike?
    fun existsByUserIdAndPostId(userId: String, postId: Long): Boolean

    fun delete(postLikeId: Long)
    fun deleteAllByPostId(postId: Long)
}