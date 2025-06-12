package noul.oe.core.comment.application

import noul.oe.core.comment.application.exception.CommentNotFoundException
import noul.oe.core.comment.application.exception.UnauthorizedCommentAccessException
import noul.oe.core.comment.application.port.input.CreateCommentCommand
import noul.oe.core.comment.application.port.input.ModifyCommentCommand
import noul.oe.core.comment.application.port.input.ReplyCommentCommand
import noul.oe.core.comment.application.port.output.CommentRepositoryPort
import noul.oe.core.comment.application.usecase.CommentCommandUseCase
import noul.oe.core.comment.domain.Comment
import noul.oe.util.WithMockCustomUser
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class CommentCommandUseCaseTest {

    private lateinit var commentRepositoryPort: CommentRepositoryPort
    private lateinit var sut: CommentCommandUseCase

    private val testUserId = "testId"
    private val otherUserId = "otherId"

    private val noneExistsCommentId = 999L
    private val now = LocalDateTime.now()

    private val testComment = Comment(
        id = 1L,
        content = "comment",
        postId = 100L,
        userId = testUserId,
        parentId = null,
        createdAt = now,
        modifiedAt = now
    )

    // 권한 없음 테스트용
    private val commentAuthByOtherUser = Comment(
        id = 2L,
        content = "comment by other user",
        postId = 101L,
        userId = otherUserId,
        parentId = null,
        createdAt = now,
        modifiedAt = now
    )


    @BeforeEach
    fun setUp() {
        commentRepositoryPort = mock()
        sut = CommentCommandUseCase(commentRepositoryPort)
    }

    @Nested
    @WithMockCustomUser
    inner class Create {
        @Test
        @DisplayName("댓글 생성에 성공한다")
        fun test100() {
            // given
            val command = CreateCommentCommand(
                content = "new content",
                postId = 100L,
                userId = testUserId
            )

            // when
            assertThatCode { sut.create(command) }
                .doesNotThrowAnyException()

            // then
            verify(commentRepositoryPort, times(1)).save(any())
        }
    }

    @Nested
    @WithMockCustomUser
    inner class Reply {

        @Test
        @DisplayName("존재하지 않는 부모 댓글에 답글 시 예외가 발생한다")
        fun test1() {
            // given
            val command = ReplyCommentCommand(
                parentId = noneExistsCommentId,
                content = "reply",
            )

            whenever(commentRepositoryPort.findById(command.parentId))
                .thenThrow(CommentNotFoundException::class.java)

            // when & then
            assertThatThrownBy { sut.reply(command) }
                .isInstanceOf(CommentNotFoundException::class.java)

            verify(commentRepositoryPort, times(1)).findById(command.parentId)
            verify(commentRepositoryPort, times(0)).save(any())
        }

        @Test
        @DisplayName("답글 생성에 성공한다")
        fun test100() {
            // given
            val command = ReplyCommentCommand(
                parentId = testComment.id,
                content = "reply",
            )

            whenever(commentRepositoryPort.findById(command.parentId)).thenReturn(testComment)

            // when
            assertThatCode { sut.reply(command) }
                .doesNotThrowAnyException()

            // then
            verify(commentRepositoryPort, times(1)).findById(command.parentId)
            verify(commentRepositoryPort, times(1)).save(any())
        }
    }

    @Nested
    @WithMockCustomUser
    inner class Modify {

        @Test
        @DisplayName("댓글 작성자가 아닌 사용자가 댓글 수정 시 예외가 발생한다")
        fun test1() {
            // given
            val command = ModifyCommentCommand(
                commentId = commentAuthByOtherUser.id,
                content = "new content"
            )

            whenever(commentRepositoryPort.findById(command.commentId)).thenReturn(commentAuthByOtherUser)

            // when & then
            assertThatThrownBy { sut.modify(command) }
                .isInstanceOf(UnauthorizedCommentAccessException::class.java)

            verify(commentRepositoryPort, times(1)).findById(command.commentId)
            verify(commentRepositoryPort, times(0)).save(any())
        }

        @Test
        @DisplayName("댓글 작성자가 자신의 댓글 수정에 성공한다")
        fun test100() {
            // given
            val command = ModifyCommentCommand(
                commentId = testComment.id,
                content = "new content"
            )

            whenever(commentRepositoryPort.findById(command.commentId)).thenReturn(testComment)

            // when
            assertThatCode { sut.modify(command) }
                .doesNotThrowAnyException()

            // then
            verify(commentRepositoryPort, times(1)).findById(command.commentId)
            verify(commentRepositoryPort, times(1)).save(testComment)
            assertThat(testComment.content).isEqualTo(command.content)
        }
    }

    @Nested
    @WithMockCustomUser
    inner class Remove {

        @Test
        @DisplayName("댓글 작성자가 아닌 사용자가 댓글 삭제 시 예외가 발생한다")
        fun test1() {
            // given
            val commentIdToRemove = commentAuthByOtherUser.id

            whenever(commentRepositoryPort.findById(commentIdToRemove)).thenReturn(commentAuthByOtherUser)

            // when & then
            assertThatThrownBy { sut.remove(commentIdToRemove) }
                .isInstanceOf(UnauthorizedCommentAccessException::class.java)
        }

        @Test
        @DisplayName("댓글 작성자가 자신의 댓글 삭제에 성공한다")
        fun test100() {
            // given
            val commentIdToRemove = testComment.id

            whenever(commentRepositoryPort.findById(commentIdToRemove)).thenReturn(testComment)
            whenever(commentRepositoryPort.findAllByParentId(commentIdToRemove)).thenReturn(emptyList())

            // when
            assertThatCode { sut.remove(commentIdToRemove) }
                .doesNotThrowAnyException()

            // then
            verify(commentRepositoryPort, times(1)).delete(commentIdToRemove)
        }
    }
}