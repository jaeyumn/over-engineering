package noul.oe.core.post.application.usecase

import noul.oe.core.post.application.exception.PostNotFoundException
import noul.oe.core.post.application.exception.PostPermissionDeniedException
import noul.oe.core.post.application.port.input.CreatePostCommand
import noul.oe.core.post.application.port.input.ModifyPostCommand
import noul.oe.core.post.application.port.input.PostCommandPort
import noul.oe.core.post.application.port.output.PostLikeRepositoryPort
import noul.oe.core.post.application.port.output.PostRepositoryPort
import noul.oe.core.post.domain.Post
import noul.oe.support.port.CommentInfoProvider
import noul.oe.support.security.SecurityUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostCommandUseCase(
    private val postRepositoryPort: PostRepositoryPort,
    private val postLikeRepositoryPort: PostLikeRepositoryPort,
    private val commentProvider: CommentInfoProvider,
) : PostCommandPort {
    override fun create(command: CreatePostCommand) {
        val userId = SecurityUtils.getCurrentUser().userId
        val post = command.toDomain(userId)
        postRepositoryPort.save(post)
    }

    override fun modify(command: ModifyPostCommand) {
        val userId = SecurityUtils.getCurrentUser().userId
        val postId = command.postId
        val post = getPost(postId)
        verifyAuthor(post.userId, userId)

        val modifiedPost = post.modify(command.title, command.content)
        postRepositoryPort.merge(modifiedPost)
    }

    override fun remove(postId: Long) {
        val userId = SecurityUtils.getCurrentUser().userId
        val post = getPost(postId)
        verifyAuthor(post.userId, userId)

        commentProvider.deleteAllComment(postId)
        postLikeRepositoryPort.deleteAllByPostId(postId)
        postRepositoryPort.delete(postId)
    }

    private fun getPost(postId: Long): Post {
        return postRepositoryPort.findById(postId)
            ?: throw PostNotFoundException(postId)
    }

    // 게시글 작성자 여부 확인
    fun verifyAuthor(authorId: String, userId: String) {
        if (authorId != userId) {
            throw PostPermissionDeniedException(userId)
        }
    }
}