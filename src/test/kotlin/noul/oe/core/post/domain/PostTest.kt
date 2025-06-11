package noul.oe.core.post.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PostTest {

    private lateinit var post: Post

    @BeforeEach
    fun setUp() {
        post = Post(userId = "testUser", title = "title", content = "content")
    }

    @Test
    fun modifyTest() {
        val newTitle = "new title"
        val newContent = "new Content"
        val modifiedPost = post.modify(newTitle, newContent)

        assertThat(modifiedPost.userId).isEqualTo(post.userId)
        assertThat(modifiedPost.title).isEqualTo(newTitle)
        assertThat(modifiedPost.content).isEqualTo(newContent)
        assertThat(modifiedPost).isNotSameAs(post)
    }

    @Test
    fun likeTest() {
        post.like()

        assertThat(post.likeCount).isEqualTo(1)
    }

    @Test
    fun unlikeTestWhenLikeCountIsZero() {
        post.unlike()

        assertThat(post.likeCount).isEqualTo(0)
    }

    @Test
    fun unlikeTest() {
        post = post.copy(likeCount = 2)
        post.unlike()

        assertThat(post.likeCount).isEqualTo(1)
    }

    @Test
    fun increaseViewCountTest() {
        post.increaseViewCount()

        assertThat(post.viewCount).isEqualTo(1)
    }
}