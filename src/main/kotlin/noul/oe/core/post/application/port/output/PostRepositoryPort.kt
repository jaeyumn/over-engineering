package noul.oe.core.post.application.port.output

import noul.oe.core.post.domain.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostRepositoryPort {
    fun save(post: Post)
    fun findById(postId: Long): Post?
    fun findAll(pageable: Pageable): Page<Post>
    fun merge(post: Post)
    fun delete(postId: Long)
}