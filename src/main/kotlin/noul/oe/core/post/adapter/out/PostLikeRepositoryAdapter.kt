package noul.oe.core.post.adapter.out

import noul.oe.core.post.application.port.output.PostLikeRepositoryPort
import noul.oe.core.post.domain.PostLike
import org.springframework.stereotype.Repository

@Repository
class PostLikeRepositoryAdapter(
    private val postLikeJpaRepository: PostLikeJpaRepository
) : PostLikeRepositoryPort {

    override fun save(postLike: PostLike) {
        val entity = PostLikeMapper.toEntity(postLike)
        postLikeJpaRepository.save(entity)
    }

    override fun findByUserIdAndPostId(userId: String, postId: Long): PostLike? {
        return postLikeJpaRepository.findByUserIdAndPostId(userId, postId)?.let {
            PostLikeMapper.toDomain(it)
        }
    }

    override fun existsByUserIdAndPostId(userId: String, postId: Long): Boolean {
        return postLikeJpaRepository.existsByUserIdAndPostId(userId, postId)
    }

    override fun delete(postLikeId: Long) {
        postLikeJpaRepository.deleteById(postLikeId)
    }

    override fun deleteAllByPostId(postId: Long) {
        postLikeJpaRepository.deleteAllByPostId(postId)
    }
}