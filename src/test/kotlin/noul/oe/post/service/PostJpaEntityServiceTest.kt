package noul.oe.post.service

import noul.oe.core.comment.repository.CommentRepository
import noul.oe.core.post.adapter.`in`.web.api.PostCreateRequest
import noul.oe.core.post.adapter.`in`.web.api.PostModifyRequest
import noul.oe.core.post.adapter.out.PostJpaEntity
import noul.oe.core.post.adapter.out.PostLikeJpaEntity
import noul.oe.core.post.exception.AlreadyLikedPostException
import noul.oe.core.post.exception.PostErrorCode.PERMISSION_DENIED
import noul.oe.core.post.exception.PostErrorCode.POST_NOT_FOUND
import noul.oe.core.post.exception.PostNotFoundException
import noul.oe.core.post.exception.PostPermissionDeniedException
import noul.oe.core.post.repository.PostLikeRepository
import noul.oe.core.post.repository.PostRepository
import noul.oe.core.user1.entity.User
import noul.oe.core.user1.repository.UserRepository
import noul.oe.support.security.UserPrincipal
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDateTime
import java.util.*

class PostJpaEntityServiceTest {
    private lateinit var sut: PostService
    private lateinit var postRepository: PostRepository
    private lateinit var postLikeRepository: PostLikeRepository
    private lateinit var userRepository: UserRepository
    private lateinit var commentRepository: CommentRepository

    private val userId = "testuser"
    private val postId = 1L
    private val userPrincipal = UserPrincipal(
        userId = userId,
        username = "testuser",
        "",
        authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
    )

    @BeforeEach
    fun setUp() {
        postRepository = mock<PostRepository>()
        postLikeRepository = mock<PostLikeRepository>()
        userRepository = mock<UserRepository>()
        commentRepository = mock<CommentRepository>()
//        sut = PostService(postRepository, postLikeRepository, userRepository, commentRepository)
    }

    @Nested
    inner class CreateTests {
        @Test
        @DisplayName("유효한 요성 시, 게시글 생성에 성공한다")
        fun test100() {
            // given
            val request = PostCreateRequest("title", "content")
            val post = request.toEntity(userId)
            whenever(postRepository.save(any<PostJpaEntity>())).thenReturn(post)

            // when
            sut.create(request)

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
            assertThatThrownBy { sut.read(postId, userPrincipal) }
                .isInstanceOf(PostNotFoundException::class.java)
                .hasMessageContaining(POST_NOT_FOUND.message)
        }

        @Test
        @DisplayName("게시글 단건 조회에 성공한다 - 조회수 증가")
        fun test100() {
            // given
            val now = LocalDateTime.now()
            val postJpaEntity = mock<PostJpaEntity> {
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
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity))

            // when
            val result = sut.read(postId, userPrincipal)

            // then
            verify(postJpaEntity).increaseViewCount()
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

            val postJpaEntity1 = mock<PostJpaEntity> {
                on { id } doReturn postId
                on { title } doReturn "title"
                on { content } doReturn "test content"
                on { createdAt } doReturn LocalDateTime.now()
                on { likeCount } doReturn 5L
                on { viewCount } doReturn 100L
                on { userId } doReturn userId
            }

            val postJpaEntity2 = mock<PostJpaEntity> {
                on { id } doReturn anotherPostId
                on { title } doReturn "another title"
                on { content } doReturn "another content"
                on { createdAt } doReturn LocalDateTime.now()
                on { likeCount } doReturn 0L
                on { viewCount } doReturn 0L
                on { userId } doReturn "another user"
            }

            val page = PageImpl(listOf(postJpaEntity1, postJpaEntity2))
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
            val postJpaEntity = mock<PostJpaEntity> { on { userId } doReturn anotherUserId }
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity))
            val request = PostModifyRequest("title", "content")

            // when & then
            assertThatThrownBy { sut.modify(postId, request) }
                .isInstanceOf(PostPermissionDeniedException::class.java)
                .hasMessageContaining(PERMISSION_DENIED.message)
        }

        @Test
        @DisplayName("유효한 요청 시, 게시글 수정에 성공한다")
        fun test100() {
            // given
            val title = "new title"
            val content = "new content"
            val postJpaEntity = mock<PostJpaEntity> {
                on { userId } doReturn userId
            }
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity))
            val request = PostModifyRequest(title, content)

            // when
            sut.modify(postId, request)

            // then
            verify(postJpaEntity).modify(title, content)
        }
    }

    @Nested
    inner class RemoveTests {
        @Test
        @DisplayName("게시글 삭제에 실패한다 - 권한 없음")
        fun test1() {
            // given
            val anotherUserId = "RANDOM_UUID"
            val postJpaEntity = mock<PostJpaEntity> { on { userId } doReturn anotherUserId }
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity))

            // when & then
            assertThatThrownBy { sut.remove(postId) }
                .isInstanceOf(PostPermissionDeniedException::class.java)
                .hasMessageContaining(PERMISSION_DENIED.message)
        }

        @Test
        @DisplayName("게시글 삭제에 성공한다")
        fun test100() {
            // given
            val postJpaEntity = mock<PostJpaEntity> { on { userId } doReturn userId }
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity))

            // when
            sut.remove(postId)

            // then
            verify(postLikeRepository).deleteAllByPostId(postId)
            verify(postRepository).delete(postJpaEntity)
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
            assertThatThrownBy { sut.like(postId) }
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
            assertThatThrownBy { sut.like(postId) }
                .isInstanceOf(AlreadyLikedPostException::class.java)
        }

        @Test
        @DisplayName("게시글에 성공적으로 좋아요를 누른다")
        fun test100() {
            // given
            val postJpaEntity = mock<PostJpaEntity> { on { increaseLikeCount() } doAnswer {} }
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity))
            whenever(postLikeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(false)

            // when
            sut.like(postId)

            // then
            verify(postLikeRepository).save(any())
            verify(postJpaEntity).increaseLikeCount()
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
            assertThatThrownBy { sut.unlike(postId) }
                .isInstanceOf(PostNotFoundException::class.java)
                .hasMessageContaining(POST_NOT_FOUND.message)
        }

        @Test
        @DisplayName("게시글에 좋아요 기록이 없으면 아무일도 일어나지 않는다")
        fun test2() {
            // given
            val postJpaEntity = mock<PostJpaEntity> {}
            whenever(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity))
            whenever(postLikeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(null)

            // when
            sut.unlike(postId)

            // then
            verify(postLikeRepository, never()).delete(any())
            verify(postJpaEntity, never()).decreaseLikeCount()
        }

        @Test
        @DisplayName("성공적으로 좋아요를 취소한다")
        fun test100() {
            // given
            val postJpaEntity = mock<PostJpaEntity> { on { decreaseLikeCount() } doAnswer {} }
            val like = mock<PostLikeJpaEntity> {
                on { userId } doReturn userId
                on { postId } doReturn postId
            }

            whenever(postRepository.findById(postId)).thenReturn(Optional.of(postJpaEntity))
            whenever(postLikeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(like)

            // when
            sut.unlike(postId)

            // then
            verify(postLikeRepository).delete(like)
            verify(postJpaEntity).decreaseLikeCount()
        }
    }
}