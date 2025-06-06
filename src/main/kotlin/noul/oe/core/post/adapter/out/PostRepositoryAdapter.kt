package noul.oe.core.post.adapter.out

import noul.oe.core.post.application.port.output.PostRepositoryPort
import noul.oe.core.post.domain.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class PostRepositoryAdapter(
    private val postJpaRepository: PostJpaRepository
) : PostRepositoryPort {

    override fun save(post: Post) {
        val entity = PostMapper.toEntity(post)
        postJpaRepository.save(entity)
    }

    override fun findById(postId: Long): Post? {
        return postJpaRepository.findById(postId)
            .map { PostMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findAll(pageable: Pageable): Page<Post> {
        return postJpaRepository.findAll(pageable)
            .map { PostMapper.toDomain(it) }
    }

    override fun merge(post: Post) {
        val entity = PostMapper.toEntity(post)
        postJpaRepository.save(entity)
    }

    override fun delete(postId: Long) {
        postJpaRepository.deleteById(postId)
    }
}