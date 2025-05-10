package noul.oe.post.service

import noul.oe.comment.repository.CommentRepository
import noul.oe.post.dto.request.PostCreateRequest
import noul.oe.post.dto.request.PostModifyRequest
import noul.oe.post.dto.response.PostDetailResponse
import noul.oe.post.dto.response.PostPageResponse
import noul.oe.post.entity.Post
import noul.oe.post.entity.PostLike
import noul.oe.post.exception.AlreadyLikedPostException
import noul.oe.post.exception.PostNotFoundException
import noul.oe.post.exception.PostPermissionDeniedException
import noul.oe.post.repository.PostLikeRepository
import noul.oe.post.repository.PostRepository
import noul.oe.user.entity.User
import noul.oe.user.exception.UserNotFoundException
import noul.oe.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
    private val postLikeRepository: PostLikeRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
) {
    @Transactional
    fun create(userId: String, request: PostCreateRequest) {
        val post = request.toEntity(userId)
        postRepository.save(post)
    }

    @Transactional
    fun read(postId: Long, userId: String): PostDetailResponse {
        val post = getPost(postId)
        post.increaseViewCount()

        val user = getUser(userId)
        val likeCount = post.likeCount
        val commentCount = commentRepository.countByPostId(postId)
        val liked = postLikeRepository.existsByUserIdAndPostId(userId, postId)

        return PostDetailResponse.from(post, user, likeCount, commentCount, liked)
    }

    fun readAll(pageable: Pageable): Page<PostPageResponse> {
        return postRepository.findAll(pageable).map { post ->
            val username = fetchUsernameByUserId(post.userId)
            val commentCount = commentRepository.countByPostId(post.id)
            PostPageResponse.from(post, username, commentCount)
        }
    }

    @Transactional
    fun modify(userId: String, postId: Long, request: PostModifyRequest) {
        val post = getPost(postId)
        verifyAuthor(userId, post)

        post.modify(
            title = request.title,
            content = request.content
        )
    }

    @Transactional
    fun remove(userId: String, postId: Long) {
        val post = getPost(postId)
        verifyAuthor(userId, post)

        postLikeRepository.deleteAllByPostId(postId)
        postRepository.delete(post)
    }

    @Transactional
    fun like(userId: String, postId: Long) {
        val post = getPost(postId)
        checkIsAlreadyLiked(userId, postId)

        val like = PostLike(userId = userId, postId = postId)
        postLikeRepository.save(like)
        post.increaseLikeCount()
    }

    @Transactional
    fun unlike(userId: String, postId: Long) {
        val post = getPost(postId)
        val like = postLikeRepository.findByUserIdAndPostId(userId, postId) ?: return

        postLikeRepository.delete(like)
        post.decreaseLikeCount()
    }

    fun fetchUsernameByUserId(userId: String): String {
        val user = getUser(userId)
        return user.username
    }

    // 게시글 작성자 여부 확인
    private fun verifyAuthor(userId: String, post: Post) {
        if (post.userId != userId) {
            throw PostPermissionDeniedException("Post permission denied by: userId=$userId")
        }
    }

    // 이미 좋아요를 눌렀는지 확인
    private fun checkIsAlreadyLiked(userId: String, postId: Long) {
        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw AlreadyLikedPostException("Already liked post by: userId=$userId, postId=$postId")
        }
    }

    private fun getUser(userId: String): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found: userId=$userId") }
    }

    private fun getPost(postId: Long): Post {
        return postRepository.findById(postId)
            .orElseThrow { PostNotFoundException("Post not found: postId=$postId") }
    }
}