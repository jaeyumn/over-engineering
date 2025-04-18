package noul.oe.comment.service

import noul.oe.comment.dto.request.CommentCreateRequest
import noul.oe.comment.entity.Comment
import noul.oe.comment.exception.CommentErrorCode.COMMENT_NOT_FOUND
import noul.oe.comment.exception.CommentErrorCode.UNAUTHORIZED_COMMENT_ACCESS
import noul.oe.comment.exception.CommentNotFoundException
import noul.oe.comment.exception.UnauthorizedCommentAccessException
import noul.oe.comment.repository.CommentRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class CommentServiceTest {
    private lateinit var sut: CommentService
    private lateinit var commentRepository: CommentRepository

    private val postId = 1L
    private val commentId = 10L
    private val userId = "test user"
    private val otherUserId = "other user"

    @BeforeEach
    fun setUp() {
        commentRepository = mock<CommentRepository>()
        sut = CommentService(commentRepository)
    }

    @Nested
    inner class CreateTests {
        @Test
        @DisplayName("댓글 생성에 성공한다")
        fun test100() {
            // given
            val request = CommentCreateRequest("내용")
            val comment = Comment(commentId, "내용", postId, userId)
            whenever(commentRepository.save(any<Comment>())).thenReturn(comment)

            // when
            val result = sut.create(postId, userId, request)

            // then
            assertThat(result.id).isEqualTo(commentId)
            assertThat(result.content).isEqualTo("내용")
            verify(commentRepository).save(any())
        }
    }

    @Nested
    inner class ReadAllTests {
        @Test
        @DisplayName("게시글의 댓글 목록을 계층 구조로 조회한다")
        fun test100() {
            // given
            val comment1 = Comment(1L, "댓글1", postId, userId)
            val reply1 = Comment(2L, "대댓글1", postId, userId, 1L)
            val comment2 = Comment(3L, "댓글2", postId, userId)
            val commentList = listOf(comment1, reply1, comment2)

            whenever(commentRepository.findAllByPostId(postId)).thenReturn(commentList)

            // when
            val result = sut.readAll(postId)

            // then
            assertThat(result).hasSize(2)
            assertThat(result[0].children).hasSize(1)
            assertThat(result[1].children).isEmpty()
        }
    }

    @Nested
    inner class ModifyTests {
        @Test
        @DisplayName("댓글 수정에 실패한다 - 권한 없음")
        fun test1() {
            // given
            val comment = Comment(commentId, "내용", postId, userId, null)
            whenever(commentRepository.findById(commentId)).thenReturn(Optional.of(comment))

            // when & then
            assertThatThrownBy { sut.modify(commentId, otherUserId, "수정") }
                .isInstanceOf(UnauthorizedCommentAccessException::class.java)
                .hasMessageContaining(UNAUTHORIZED_COMMENT_ACCESS.message)
        }

        @Test
        @DisplayName("댓글 수정에 실패한다 - 존재하지 않음")
        fun test2() {
            // given
            whenever(commentRepository.findById(commentId)).thenReturn(Optional.empty())

            // when & then
            assertThatThrownBy { sut.modify(commentId, userId, "수정") }
                .isInstanceOf(CommentNotFoundException::class.java)
                .hasMessageContaining(COMMENT_NOT_FOUND.message)
        }

        @Test
        @DisplayName("댓글 수정에 성공한다")
        fun test100() {
            // given
            val comment = Comment(commentId, "내용", postId, userId)
            whenever(commentRepository.findById(commentId)).thenReturn(Optional.of(comment))

            // when
            sut.modify(commentId, userId, "수정된 내용")

            // then
            assertThat(comment.content).isEqualTo("수정된 내용")
        }
    }

    @Nested
    inner class RemoveTests {
        @Test
        @DisplayName("댓글 삭제에 성공한다")
        fun test100() {
            // given
            val comment = Comment(commentId, "내용", postId, userId, null)
            whenever(commentRepository.findById(commentId)).thenReturn(Optional.of(comment))

            // when
            sut.remove(commentId, userId)

            // then
            verify(commentRepository).delete(comment)
        }

        @Test
        @DisplayName("댓글 삭제에 실패한다 - 권한 없음")
        fun test1() {
            // given
            val comment = Comment(commentId, "내용", postId, userId, null)
            whenever(commentRepository.findById(commentId)).thenReturn(Optional.of(comment))

            // when & then
            assertThatThrownBy { sut.remove(commentId, otherUserId) }
                .isInstanceOf(UnauthorizedCommentAccessException::class.java)
                .hasMessageContaining(UNAUTHORIZED_COMMENT_ACCESS.message)
        }

        @Test
        @DisplayName("댓글 삭제에 실패한다 - 존재하지 않음")
        fun test2() {
            // given
            whenever(commentRepository.findById(commentId)).thenReturn(Optional.empty())

            // when & then
            assertThatThrownBy { sut.remove(commentId, userId) }
                .isInstanceOf(CommentNotFoundException::class.java)
                .hasMessageContaining(COMMENT_NOT_FOUND.message)
        }
    }
}