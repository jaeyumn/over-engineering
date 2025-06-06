package noul.oe.core.post.application.usecase

import noul.oe.core.post.application.exception.AlreadyLikedPostException
import noul.oe.core.post.application.exception.PostLikeNotFoundException
import noul.oe.core.post.application.exception.PostNotFoundException
import noul.oe.core.post.application.port.input.PostLikeCommandPort
import noul.oe.core.post.application.port.output.PostLikeRepositoryPort
import noul.oe.core.post.application.port.output.PostRepositoryPort
import noul.oe.core.post.domain.Post
import noul.oe.core.post.domain.PostLike
import noul.oe.support.security.SecurityUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostLikeCommandUseCase(
    private val postRepositoryPort: PostRepositoryPort,
    private val postLikeRepositoryPort: PostLikeRepositoryPort,
) : PostLikeCommandPort {

    override fun like(postId: Long) {
        val userId = SecurityUtils.getCurrentUser().userId

        val post = getPost(postId)
        checkIsAlreadyLiked(userId, postId)

        post.like()
        postRepositoryPort.save(post)

        val postLike = PostLike(userId = userId, postId = postId)
        postLikeRepositoryPort.save(postLike)
    }

    override fun unlike(postId: Long) {
        val userId = SecurityUtils.getCurrentUser().userId

        val post = getPost(postId)
        post.unlike()
        postRepositoryPort.save(post)

        val postLike = postLikeRepositoryPort.findByUserIdAndPostId(userId, postId)
            ?: throw PostLikeNotFoundException(postId)
        postLikeRepositoryPort.delete(postLike.id!!)
    }

    private fun getPost(postId: Long): Post {
        return postRepositoryPort.findById(postId) ?: throw PostNotFoundException(postId)
    }

    // 이미 좋아요를 눌렀는지 확인
    private fun checkIsAlreadyLiked(userId: String, postId: Long) {
        if (postLikeRepositoryPort.existsByUserIdAndPostId(userId, postId)) {
            throw AlreadyLikedPostException(userId)
        }
    }
}