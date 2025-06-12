package noul.oe.core.comment.adapter

import noul.oe.core.comment.adapter.out.info.CommentInfoProviderImpl
import noul.oe.core.comment.adapter.out.persistence.CommentJpaEntity
import noul.oe.core.comment.adapter.out.persistence.CommentJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class CommentInfoProviderImplTest {

    @Mock
    lateinit var commentRepository: CommentJpaRepository

    lateinit var sut: CommentInfoProviderImpl

    private val testPostId = 100L
    private val testUserId = "testUserId"
    private val anotherUserId = "anotherUserId"
    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        sut = CommentInfoProviderImpl(commentRepository)
    }

    @Test
    @DisplayName("댓글 수를 정상적으로 반환한다")
    fun getCommentCountTest() {
        // given
        val expectedCount = 5
        whenever(commentRepository.countByPostId(testPostId)).thenReturn(expectedCount)

        // when
        val result = sut.getCommentCount(testPostId)

        // then
        assertThat(result).isEqualTo(expectedCount)
        verify(commentRepository, times(1)).countByPostId(testPostId)
    }

    @Test
    @DisplayName("답글을 포함한 댓글 목록을 계층 구조로 반환한다")
    fun getCommentListTest() {
        // given
        val rootComment1 = CommentJpaEntity(1L, "루트댓글1", testPostId, testUserId, null, now, now)
        val reply1_1 = CommentJpaEntity(2L, "답글1-1", testPostId, testUserId, rootComment1.id, now, now)
        val reply1_2 = CommentJpaEntity(3L, "답글1-2", testPostId, anotherUserId, rootComment1.id, now, now)
        val rootComment2 = CommentJpaEntity(4L, "루트댓글2", testPostId, anotherUserId, null, now, now)
        val comments = listOf(rootComment1, reply1_1, reply1_2, rootComment2)
        whenever(commentRepository.findAllByPostId(testPostId)).thenReturn(comments)

        // when
        val result = sut.getCommentList(testPostId, testUserId)

        // then
        assertThat(result).hasSize(2)
        val actualRoot1 = result.find { it.id == rootComment1.id }
        assertThat(actualRoot1).isNotNull
        assertThat(actualRoot1!!.children).hasSize(2)
        assertThat(actualRoot1.children.find { it.id == reply1_1.id }!!.editable).isTrue()
        assertThat(actualRoot1.children.find { it.id == reply1_2.id }!!.editable).isFalse()
        verify(commentRepository, times(1)).findAllByPostId(testPostId)
    }

    @Test
    @DisplayName("모든 댓글을 성공적으로 삭제한다")
    fun deleteAllCommentTest() {
        // when
        sut.deleteAllComment(testPostId)

        // then
        verify(commentRepository, times(1)).deleteAllByPostId(testPostId)
    }
}