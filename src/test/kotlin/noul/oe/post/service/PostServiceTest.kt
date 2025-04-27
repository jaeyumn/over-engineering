package noul.oe.post.service

import noul.oe.comment.repository.CommentRepository
import noul.oe.post.dto.request.PostCreateRequest
import noul.oe.post.dto.request.PostModifyRequest
import noul.oe.post.entity.Post
import noul.oe.post.entity.PostLike
import noul.oe.post.exception.AlreadyLikedPostException
import noul.oe.post.exception.PostErrorCode.PERMISSION_DENIED
import noul.oe.post.exception.PostErrorCode.POST_NOT_FOUND
import noul.oe.post.exception.PostNotFoundException
import noul.oe.post.exception.PostPermissionDeniedException
import noul.oe.post.repository.PostLikeRepository
import noul.oe.post.repository.PostRepository
import noul.oe.user.entity.User
import noul.oe.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.*

class PostServiceTest {
    private lateinit var sut: PostService
    private lateinit var postRepository: PostRepository
    private lateinit var postLikeRepository: PostLikeRepository
    private lateinit var userRepository: UserRepository
    private lateinit var commentRepository: CommentRepository

    private val userId = "testuser"
    private val postId = 1L

    @BeforeEach
    fun setUp() {
        postRepository = mock<PostRepository>()
        postLikeRepository = mock<PostLikeRepository>()
        userRepository = mock<UserRepository>()
        commentRepository = mock<CommentRepository>()
        sut = PostService(postRepository, postLikeRepository, userRepository, commentRepository)
    }

    @Nested
    inner class CreateTests {
        @Test
        @DisplayName("유효한 요성 시, 게시글 생성에 성공한다")
        fun test100() {
            // given
            val request = PostCreateRequest("title", "content")
            val post = request.toEntity(userId)
            whenever(postRepository.save(any<Post>())).thenReturn(post)

            // when
            sut.create(userId, request)

            // then
            verify(postRepository).save(any())
        }
    }

    @Nested
    inner class ReadTest {
        @Test
        @DisplayName("게시글이 존재하지 않으면 PostNotFoundException이 발생한다")
        fun test1() {
            // given
            whenever(postRepository.findById(postId)).thenReturn(Optional.empty())

            // when & then
            assertThatThrownBy { sut.read(postId, userId) }
                .isInstanceOf(PostNotFoundException::class.java)
                .hasMessageContaining(POST_NOT_FOUND.message)
        }

        @Test
        @DisplayName("게시글 단건 조회에 성공한다 - 조회수 증가")
        fun test100() {
            // given
            val now = LocalDateTime.now()
            val post = mock<Post> {
                on { id } doReturn postId
                on { userId } doReturn userId
                on { title } doReturn "title"
                on { content } doReturn "content"
                on { viewCount } doReturn 0
                on { likeCount } doReturn 0
                on { createdAt } doReturn now
                on { modifiedAt } doReturn now
                on { increaseViewCount() } doAnswer {}
            }
            val user = mock<User> {
                on { username } doReturn "test-username"
            }
            whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(post))

            // when
            val result = sut.read(postId, userId)

            // then
            verify(post).increaseViewCount()
            assertThat(result).isNotNull()
        }
    }

    @Nested
    inner class ReadAllTest {
        @Test
        @DisplayName("게시글 목록 조회에 성공한다 - 페이징")
        fun test100() {
            // given
            val anotherPostId = 2L
            val pageable = PageRequest.of(0, 10)

            val post1 = mock<Post> {
                on { id } doReturn postId
                on { title } doReturn "title"
                on { content } doReturn "test content"
                on { createdAt } doReturn LocalDateTime.now()
                on { likeCount } doReturn 5L
                on { viewCount } doReturn 100L
                on { userId } doReturn userId
            }

            val post2 = mock<Post> {
                on { id } doReturn anotherPostId
                on { title } doReturn "another title"
                on { content } doReturn "another content"
                on { createdAt } doReturn LocalDateTime.now()
                on { likeCount } doReturn 0L
                on { viewCount } doReturn 0L
                on { userId } doReturn "another user"
            }

            val page = PageImpl(listOf(post1, post2))
            whenever(postRepository.findAll(pageable)).thenReturn(page)

            val user = mock<User> {
                on { username } doReturn "user"
            }
            whenever(userRepository.findById(any())).thenReturn(Optional.of(user))

            // when
            val result = sut.readAll(pageable)

            // then
            assertThat(result.content).hasSize(2)
            assertThat(result.content[0].title).isEqualTo("title")
            assertThat(result.content[1].title).isEqualTo("another title")
        }
    }

    @Nested
    inner class ModifyTests {
        @Test
        @DisplayName("게시글 수정에 실패한다 - 권한 없음")
        fun test1() {
            // given
            val anotherUserId = "RANDOM_UUID"
            val post = mock<Post> { on { userId } doReturn anotherUserId }
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(post))
            val request = PostModifyRequest("title", "content")

            // when & then
            assertThatThrownBy { sut.modify(userId, postId, request) }
                .isInstanceOf(PostPermissionDeniedException::class.java)
                .hasMessageContaining(PERMISSION_DENIED.message)
        }

        @Test
        @DisplayName("유효한 요청 시, 게시글 수정에 성공한다")
        fun test100() {
            // given
            val title = "new title"
            val content = "new content"
            val post = mock<Post> {
                on { userId } doReturn userId
            }
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(post))
            val request = PostModifyRequest(title, content)

            // when
            sut.modify(userId, postId, request)

            // then
            verify(post).modify(title, content)
        }
    }

    @Nested
    inner class RemoveTests {
        @Test
        @DisplayName("게시글 삭제에 실패한다 - 권한 없음")
        fun test1() {
            // given
            val anotherUserId = "RANDOM_UUID"
            val post = mock<Post> { on { userId } doReturn anotherUserId }
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(post))

            // when & then
            assertThatThrownBy { sut.remove(userId, postId) }
                .isInstanceOf(PostPermissionDeniedException::class.java)
                .hasMessageContaining(PERMISSION_DENIED.message)
        }

        @Test
        @DisplayName("게시글 삭제에 성공한다")
        fun test100() {
            // given
            val post = mock<Post> { on { userId } doReturn userId }
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(post))

            // when
            sut.remove(userId, postId)

            // then
            verify(postLikeRepository).deleteAllByPostId(postId)
            verify(postRepository).delete(post)
        }
    }

    @Nested
    inner class LikeTests {
        @Test
        @DisplayName("게시글이 존재하지 않으면 PostNotFoundException이 발생한다")
        fun test1() {
            // given
            whenever(postRepository.findById(postId)).thenReturn(Optional.empty())

            // when & then
            assertThatThrownBy { sut.like(userId, postId) }
                .isInstanceOf(PostNotFoundException::class.java)
                .hasMessageContaining(POST_NOT_FOUND.message)
        }

        @Test
        @DisplayName("게시글에 이미 좋아요를 누른 상태면 AlreadyLikedPostException이 발생한다")
        fun test2() {
            // given
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(mock()))
            whenever(postLikeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(true)

            // when & then
            assertThatThrownBy { sut.like(userId, postId) }
                .isInstanceOf(AlreadyLikedPostException::class.java)
        }

        @Test
        @DisplayName("게시글에 성공적으로 좋아요를 누른다")
        fun test100() {
            // given
            val post = mock<Post> { on { increaseLikeCount() } doAnswer {} }
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(post))
            whenever(postLikeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(false)

            // when
            sut.like(userId, postId)

            // then
            verify(postLikeRepository).save(any())
            verify(post).increaseLikeCount()
        }
    }

    @Nested
    inner class UnlikeTests {
        @Test
        @DisplayName("게시글이 존재하지 않으면 PostNotFoundException이 발생한다")
        fun test1() {
            // given
            whenever(postRepository.findById(postId)).thenReturn(Optional.empty())

            // when & then
            assertThatThrownBy { sut.unlike(userId, postId) }
                .isInstanceOf(PostNotFoundException::class.java)
                .hasMessageContaining(POST_NOT_FOUND.message)
        }

        @Test
        @DisplayName("게시글에 좋아요 기록이 없으면 아무일도 일어나지 않는다")
        fun test2() {
            // given
            val post = mock<Post> {}
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(post))
            whenever(postLikeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(null)

            // when
            sut.unlike(userId, postId)

            // then
            verify(postLikeRepository, never()).delete(any())
            verify(post, never()).decreaseLikeCount()
        }

        @Test
        @DisplayName("성공적으로 좋아요를 취소한다")
        fun test100() {
            // given
            val post = mock<Post> { on { decreaseLikeCount() } doAnswer {} }
            val like = mock<PostLike> {
                on { userId } doReturn userId
                on { postId } doReturn postId
            }

            whenever(postRepository.findById(postId)).thenReturn(Optional.of(post))
            whenever(postLikeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(like)

            // when
            sut.unlike(userId, postId)

            // then
            verify(postLikeRepository).delete(like)
            verify(post).decreaseLikeCount()
        }
    }
}