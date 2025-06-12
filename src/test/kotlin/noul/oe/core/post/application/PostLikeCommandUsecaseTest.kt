package noul.oe.core.post.application

import noul.oe.core.post.application.exception.AlreadyLikedPostException
import noul.oe.core.post.application.exception.PostNotFoundException
import noul.oe.core.post.application.port.output.PostLikeRepositoryPort
import noul.oe.core.post.application.port.output.PostRepositoryPort
import noul.oe.core.post.application.usecase.PostLikeCommandUseCase
import noul.oe.core.post.domain.Post
import noul.oe.core.post.domain.PostLike
import noul.oe.util.WithMockCustomUser
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class PostLikeCommandUsecaseTest {

    private lateinit var postRepositoryPort: PostRepositoryPort
    private lateinit var postLikeRepositoryPort: PostLikeRepositoryPort
    private lateinit var sut: PostLikeCommandUseCase

    private val existsPostId = 1L
    private val noneExistsPostId = 1004L
    private val userId = "testId"
    private val now = LocalDateTime.now()

    private val testPost = Post(
        id = 1L,
        userId = userId,
        title = "title",
        content = "content",
        createdAt = now,
    )

    @BeforeEach
    fun setUp() {
        postRepositoryPort = mock()
        postLikeRepositoryPort = mock()
        sut = PostLikeCommandUseCase(postRepositoryPort, postLikeRepositoryPort)
    }

    @Nested
    @WithMockCustomUser
    inner class Like {

        @Test
        @DisplayName("존재하지 않는 게시글이면 예외가 발생한다")
        fun test1() {
            // given
            whenever(postRepositoryPort.findById(noneExistsPostId)).thenReturn(null)

            // when & then
            assertThatThrownBy { sut.like(noneExistsPostId) }
                .isInstanceOf(PostNotFoundException::class.java)
        }

        @Test
        @DisplayName("이미 좋아요한 게시글이면 예외가 발생한다")
        fun test2() {
            // given
            whenever(postRepositoryPort.findById(existsPostId))
                .thenReturn(testPost)
            whenever(postLikeRepositoryPort.existsByUserIdAndPostId("testId", existsPostId))
                .thenReturn(true)

            // when & then
            assertThatThrownBy { sut.like(existsPostId) }
                .isInstanceOf(AlreadyLikedPostException::class.java)
        }

        @Test
        @DisplayName("게시글 좋아요에 성공한다")
        fun test100() {
            // given
            val testPostLike = PostLike(
                userId = userId,
                postId = 1L,
            )
            whenever(postRepositoryPort.findById(existsPostId)).thenReturn(testPost)
            whenever(postLikeRepositoryPort.existsByUserIdAndPostId("testId", existsPostId)).thenReturn(false)

            // when
            sut.like(existsPostId)

            // then
            verify(postRepositoryPort).save(testPost)
            verify(postLikeRepositoryPort).save(testPostLike)
        }
    }

    @Nested
    @WithMockCustomUser
    inner class UnLike {

        @Test
        @DisplayName("존재하지 않는 게시글이면 예외가 발생한다")
        fun test1() {
            // given
            whenever(postRepositoryPort.findById(noneExistsPostId)).thenReturn(null)

            // when & then
            assertThatThrownBy { sut.unlike(noneExistsPostId) }
                .isInstanceOf(PostNotFoundException::class.java)
        }

        @Test
        @DisplayName("게시글 좋아요 취소에 성공한다")
        fun test100() {
            // given
            val postId = 1L
            val testPostLike = PostLike(
                id = 1L,
                userId = userId,
                postId = postId,
            )
            whenever(postRepositoryPort.findById(existsPostId)).thenReturn(testPost)
            whenever(postLikeRepositoryPort.findByUserIdAndPostId("testId", postId)).thenReturn(testPostLike)

            // when
            sut.unlike(postId)

            // then
            verify(postRepositoryPort).save(any())
            verify(postLikeRepositoryPort).delete(testPostLike.id!!)
        }
    }
}