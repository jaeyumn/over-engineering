package noul.oe.core.post.application

import noul.oe.core.post.application.exception.PostNotFoundException
import noul.oe.core.post.application.exception.PostPermissionDeniedException
import noul.oe.core.post.application.port.input.CreatePostCommand
import noul.oe.core.post.application.port.input.ModifyPostCommand
import noul.oe.core.post.application.port.output.PostLikeRepositoryPort
import noul.oe.core.post.application.port.output.PostRepositoryPort
import noul.oe.core.post.application.usecase.PostCommandUseCase
import noul.oe.core.post.domain.Post
import noul.oe.support.port.CommentInfoProvider
import noul.oe.util.WithMockCustomUser
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class PostCommandUseCaseTest {

    private lateinit var postRepositoryPort: PostRepositoryPort
    private lateinit var postLikeRepositoryPort: PostLikeRepositoryPort
    private lateinit var commentProvider: CommentInfoProvider
    private lateinit var sut: PostCommandUseCase

    private val userId = "testId"
    private val otherUserId = "otherUser"
    private val noneExistsPostId = 1004L
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
        commentProvider = mock()
        sut = PostCommandUseCase(postRepositoryPort, postLikeRepositoryPort, commentProvider)
    }

    @Nested
    @WithMockCustomUser
    inner class Create {

        @Test
        @DisplayName("게시글 생성에 성공한다")
        fun test100() {
            // given
            val command = CreatePostCommand("title", "content")

            // when
            sut.create(command)

            // then
            verify(postRepositoryPort).save(any())
        }
    }

    @Nested
    @WithMockCustomUser
    inner class Modify {

        @Test
        @DisplayName("존재하지 않는 게시글 수정 시 예외가 발생한다")
        fun test1() {
            // given
            val command = ModifyPostCommand(noneExistsPostId, "new title", "new content")
            whenever(postRepositoryPort.findById(command.postId)).thenReturn(null)

            // when & then
            assertThatThrownBy { sut.modify(command) }
                .isInstanceOf(PostNotFoundException::class.java)
        }

        @Test
        @DisplayName("작성자가 아닌 사용자가 게시글 수정 시 예외가 발생한다")
        fun test2() {
            // given
            val command = ModifyPostCommand(1L, "new title", "new content")
            whenever(postRepositoryPort.findById(command.postId))
                .thenReturn(testPost.copy(userId = otherUserId))

            // when & then
            assertThatThrownBy { sut.modify(command) }
                .isInstanceOf(PostPermissionDeniedException::class.java)
        }

        @Test
        @DisplayName("게시글 수정에 성공한다")
        fun test100() {
            // given
            val command = ModifyPostCommand(1L, "new title", "new content")
            whenever(postRepositoryPort.findById(command.postId)).thenReturn(testPost)

            // when
            sut.modify(command)

            // then
            verify(postRepositoryPort).merge(any())
        }
    }

    @Nested
    @WithMockCustomUser
    inner class Remove {

        @Test
        @DisplayName("존재하지 않는 게시글 삭제 시 예외가 발생한다")
        fun test1() {
            // given
            whenever(postRepositoryPort.findById(noneExistsPostId)).thenReturn(null)

            // when & then
            assertThatThrownBy { sut.remove(noneExistsPostId) }
                .isInstanceOf(PostNotFoundException::class.java)
        }

        @Test
        @DisplayName("작성자가 아닌 사용자가 게시글 삭제 시 예외가 발생한다")
        fun test2() {
            // given
            val postId = 1L
            whenever(postRepositoryPort.findById(postId))
                .thenReturn(testPost.copy(userId = otherUserId))

            // when & then
            assertThatThrownBy { sut.remove(postId) }
                .isInstanceOf(PostPermissionDeniedException::class.java)
        }

        @Test
        @DisplayName("게시글 삭제에 성공한다")
        fun test100() {
            // given
            val postId = 1L
            whenever(postRepositoryPort.findById(postId)).thenReturn(testPost)

            // when
            sut.remove(postId)

            // then
            verify(commentProvider).deleteAllComment(postId)
            verify(postLikeRepositoryPort).deleteAllByPostId(postId)
            verify(postRepositoryPort).delete(postId)
        }
    }
}