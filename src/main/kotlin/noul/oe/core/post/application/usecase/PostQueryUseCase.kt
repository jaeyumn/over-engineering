package noul.oe.core.post.application.usecase

import noul.oe.core.post.application.exception.PostNotFoundException
import noul.oe.core.post.application.port.input.*
import noul.oe.core.post.application.port.output.PostLikeRepositoryPort
import noul.oe.core.post.application.port.output.PostRepositoryPort
import noul.oe.core.post.domain.Post
import noul.oe.support.port.CommentInfoProvider
import noul.oe.support.port.UserInfoProvider
import noul.oe.support.security.SecurityUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostQueryUseCase(
    private val postRepositoryPort: PostRepositoryPort,
    private val postLikeRepository: PostLikeRepositoryPort,
    private val userInfoProvider: UserInfoProvider,
    private val commentInfoProvider: CommentInfoProvider,
) : PostQueryPort {

    override fun readDetail(postId: Long): PostDetailResponse {
        val user = SecurityUtils.getCurrentUser()
        val post = getPost(postId)
        post.increaseViewCount()

        val commentCount = commentInfoProvider.getCommentCount(postId)
        val liked = postLikeRepository.existsByUserIdAndPostId(user.userId, post.id!!)
        val editable = user.userId == post.userId

        return PostDetailResponse(
            postId, user.userId, user.username, editable, post.title, post.content,
            post.viewCount, post.likeCount, commentCount, liked, createdAt = post.createdAt
        )
    }

    override fun readAll(pageable: Pageable): Page<PostPageResponse> {
        return postRepositoryPort.findAll(pageable).map { post ->
            val username = userInfoProvider.getUsername(post.userId)
            val commentCount = commentInfoProvider.getCommentCount(post.id!!)
            PostPageResponse(
                post.id, post.title, post.content, username,
                post.createdAt, post.likeCount, commentCount, post.viewCount
            )
        }
    }

    override fun readWithComments(postId: Long): PostDetailWithCommentsResponse {
        val user = SecurityUtils.getCurrentUser()
        val post = getPost(postId)
        val commentCount = commentInfoProvider.getCommentCount(postId)
        val liked = postLikeRepository.existsByUserIdAndPostId(user.userId, post.id!!)
        val commentList = commentInfoProvider.getCommentList(post.id, user.userId).map {
            it.copy(username = userInfoProvider.getUsername(it.userId))
        }
        val editable = post.userId == user.userId
        val postDetail = PostDetailResponse(
            post.id, user.userId, user.username, editable, post.title, post.content,
            post.viewCount, post.likeCount, commentCount, liked, createdAt = post.createdAt
        )

        return PostDetailWithCommentsResponse(postDetail, commentList)
    }

    private fun getPost(postId: Long): Post {
        return postRepositoryPort.findById(postId) ?: throw PostNotFoundException(postId)
    }
}