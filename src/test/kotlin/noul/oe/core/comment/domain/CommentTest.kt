package noul.oe.core.comment.domain

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommentTest {

    private lateinit var comment: Comment

    @BeforeEach
    fun setUp() {
        comment = Comment(
            id = 1L,
            content = "content",
            postId = 1L,
            userId = "testId",
        )
    }

    @Test
    fun modifyTest() {
        // given
        val newContent = "new content"

        // when
        comment.modify(newContent)

        // then
        assertThat(comment.content).isEqualTo(newContent)
    }

    @Test
    fun isNotOwnedByTest() {
        // given
        val otherUserId = "otherUserId"

        // when
        val result = comment.isNotOwnedBy(otherUserId)

        // then
        assertThat(result).isTrue()
    }
}