package noul.oe.domain.post.service

import noul.oe.domain.post.dto.request.PostCreateRequest
import noul.oe.domain.post.dto.request.PostModifyRequest
import noul.oe.domain.post.dto.response.PostDetailResponse
import noul.oe.domain.post.dto.response.PostDetailWithCommentsResponse
import noul.oe.domain.post.dto.response.PostPageResponse
import noul.oe.domain.post.entity.Post
import noul.oe.domain.post.entity.PostLike
import noul.oe.domain.post.exception.AlreadyLikedPostException
import noul.oe.domain.post.exception.PostNotFoundException
import noul.oe.domain.post.exception.PostPermissionDeniedException
import noul.oe.domain.post.repository.PostLikeRepository
import noul.oe.domain.post.repository.PostRepository
import noul.oe.support.info.CommentInfoProvider
import noul.oe.support.info.UserInfoProvider
import noul.oe.support.security.SecurityUtils
import noul.oe.support.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
    private val postLikeRepository: PostLikeRepository,
    private val userInfoProvider: UserInfoProvider,
    private val commentInfoProvider: CommentInfoProvider,
) {
    @Transactional
    fun create(request: PostCreateRequest) {
        val userId = SecurityUtils.getCurrentUser().userId
        val post = request.toEntity(userId)
        postRepository.save(post)
    }

    @Transactional
    fun read(postId: Long, user: UserPrincipal): PostDetailResponse {
        val post = getPost(postId)
        post.increaseViewCount()

        val likeCount = post.likeCount
        val commentCount = commentInfoProvider.getCommentCount(postId)
        val liked = postLikeRepository.existsByUserIdAndPostId(user.userId, postId)

        return PostDetailResponse.from(post, user.username, likeCount, commentCount, liked)
    }

    fun readAll(pageable: Pageable): Page<PostPageResponse> {
        return postRepository.findAll(pageable).map { post ->
            val username = userInfoProvider.getUsername(post.userId)
            val commentCount = commentInfoProvider.getCommentCount(post.id)
            PostPageResponse.from(post, username, commentCount)
        }
    }

    fun readWithComments(postId: Long, user: UserPrincipal): PostDetailWithCommentsResponse {
        val post = getPost(postId)
        val likeCount = post.likeCount
        val commentCount = commentInfoProvider.getCommentCount(postId)
        val liked = postLikeRepository.existsByUserIdAndPostId(user.userId, postId)
        val commentList = commentInfoProvider.getCommentList(postId, user.userId).map {
            it.copy(username = userInfoProvider.getUsername(it.userId))
        }
        val postDetail = PostDetailResponse.from(post, user.username, likeCount, commentCount, liked)

        return PostDetailWithCommentsResponse(postDetail, commentList)
    }

    @Transactional
    fun modify(postId: Long, request: PostModifyRequest) {
        val userId = SecurityUtils.getCurrentUser().userId
        val post = getPost(postId)
        verifyAuthor(userId, post)

        post.modify(
            title = request.title,
            content = request.content
        )
    }

    @Transactional
    fun remove(postId: Long) {
        val userId = SecurityUtils.getCurrentUser().userId
        val post = getPost(postId)
        verifyAuthor(userId, post)

        postLikeRepository.deleteAllByPostId(postId)
        postRepository.delete(post)
    }

    @Transactional
    fun like(postId: Long) {
        val userId = SecurityUtils.getCurrentUser().userId
        val post = getPost(postId)
        checkIsAlreadyLiked(userId, postId)

        val like = PostLike(userId = userId, postId = postId)
        postLikeRepository.save(like)
        post.increaseLikeCount()
    }

    @Transactional
    fun unlike(postId: Long) {
        val userId = SecurityUtils.getCurrentUser().userId
        val post = getPost(postId)
        val like = postLikeRepository.findByUserIdAndPostId(userId, postId) ?: return

        postLikeRepository.delete(like)
        post.decreaseLikeCount()
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

    private fun getPost(postId: Long): Post {
        return postRepository.findById(postId)
            .orElseThrow { PostNotFoundException("Post not found: postId=$postId") }
    }
}