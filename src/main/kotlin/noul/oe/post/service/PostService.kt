package noul.oe.post.service

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
) {
    @Transactional
    fun create(userId: String, request: PostCreateRequest) {
        val post = request.toEntity(userId)
        postRepository.save(post)
    }

    fun read(postId: Long): PostDetailResponse {
        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException() }
        post.increaseViewCount()
        return PostDetailResponse.from(post)
    }

    fun readAll(pageable: Pageable): Page<PostPageResponse> {
        return postRepository.findAll(pageable).map { post ->
            val username = fetchUsernameByUserId(post.userId)
            val commentCount = 0
            PostPageResponse.from(post, username, commentCount)
        }
    }

    @Transactional
    fun modify(userId: String, postId: Long, request: PostModifyRequest) {
        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException() }
        verifyAuthor(userId, post)

        post.modify(
            title = request.title,
            content = request.content
        )
    }

    @Transactional
    fun remove(userId: String, postId: Long) {
        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException() }
        verifyAuthor(userId, post)

        postLikeRepository.deleteAllByPostId(postId)
        postRepository.delete(post)
    }

    @Transactional
    fun like(userId: String, postId: Long) {
        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException() }
        checkIsAlreadyLiked(userId, postId)

        val like = PostLike(userId = userId, postId = postId)
        postLikeRepository.save(like)
        post.increaseLikeCount()
    }

    @Transactional
    fun unlike(userId: String, postId: Long) {
        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException() }
        val like = postLikeRepository.findByUserIdAndPostId(userId, postId) ?: return

        postLikeRepository.delete(like)
        post.decreaseLikeCount()
    }

    fun fetchUsernameByUserId(userId: String): String {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException() }
        return user.username
    }

    // 게시글 작성자 여부 확인
    private fun verifyAuthor(userId: String, post: Post) {
        if (post.userId != userId) {
            throw PostPermissionDeniedException()
        }
    }

    // 이미 좋아요를 눌렀는지 확인
    private fun checkIsAlreadyLiked(userId: String, postId: Long) {
        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw AlreadyLikedPostException()
        }
    }
}