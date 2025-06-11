package noul.oe.core.post.adapter

import noul.oe.core.post.adapter.out.PostJpaRepository
import noul.oe.core.post.adapter.out.PostRepositoryAdapter
import noul.oe.core.post.domain.Post
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@Import(PostRepositoryAdapter::class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class PostRepositoryAdapterTest {

    @Autowired
    lateinit var sut: PostRepositoryAdapter

    @Autowired
    lateinit var postJpaRepository: PostJpaRepository

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var testUserId: String
    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        testUserId = UUID.randomUUID().toString()

        // 초기화
        postJpaRepository.deleteAll()
        jdbcTemplate.update("DELETE FROM users")

        // 테스트 데이터 삽입
        insertTestUserData()
    }

    private fun createPost(
        id: Long = 1L,
        userId: String = testUserId,
        title: String = "title",
        content: String = "content",
    ): Post {
        return Post(
            id = id,
            userId = userId,
            title = title,
            content = content,
        )
    }

    @Test
    fun saveTest() {
        // given
        val post = createPost(id = 1L)

        // when
        sut.save(post)

        // then
        val saved = postJpaRepository.findById(post.id!!).orElse(null)
        assertThat(saved).isNotNull
        assertThat(saved.userId).isEqualTo(post.userId)
        assertThat(saved.title).isEqualTo(post.title)
        assertThat(saved.content).isEqualTo(post.content)
    }

    @Test
    fun findByIdTest() {
        // given
        val post = createPost(id = 2L)
        insertTestPostData(post)

        // when
        val foundPost = sut.findById(post.id!!)

        // then
        assertThat(foundPost).isNotNull
        assertThat(foundPost!!.id).isEqualTo(post.id)
        assertThat(foundPost.title).isEqualTo(post.title)
    }

    @Test
    fun findAllTest() {
        // given
        val post1 = createPost(id = 3L, title = "Post 1")
        val post2 = createPost(id = 4L, title = "Post 2")
        sut.save(post1)
        sut.save(post2)

        val pageable = PageRequest.of(0, 10)

        // when
        val page = sut.findAll(pageable)

        // then
        assertThat(page).isNotNull
        assertThat(page.totalElements).isEqualTo(2)
        assertThat(page.content).hasSize(2)
        assertThat(page.content[0].title).isEqualTo("Post 1")
        assertThat(page.content[1].title).isEqualTo("Post 2")
    }

    @Test
    fun mergeTest() {
        // given
        val originalPost = createPost(id = 5L, title = "Original Title", content = "Original Content")
        sut.save(originalPost)

        val modifiedPost = originalPost.copy(title = "Updated Title", content = "Updated Content")

        // when
        sut.merge(modifiedPost)

        // then
        val merged = postJpaRepository.findById(modifiedPost.id!!).orElse(null)
        assertThat(merged).isNotNull
        assertThat(merged.title).isEqualTo("Updated Title")
        assertThat(merged.content).isEqualTo("Updated Content")
    }

    @Test
    fun deleteTest() {
        // given
        val postToDelete = createPost(id = 6L)
        sut.save(postToDelete)
        assertThat(postJpaRepository.findById(postToDelete.id!!).isPresent).isTrue()

        // when
        sut.delete(postToDelete.id!!)

        // then
        assertThat(postJpaRepository.findById(postToDelete.id!!).isPresent).isFalse()
    }

    private fun insertTestUserData() {
        val sql = """
            INSERT INTO users(id, username, email, password, role, created_at, modified_at)
            VALUES(?, ?, ?, ?, ?, ?, ?)
        """

        jdbcTemplate.update(
            sql.trimIndent(),
            testUserId,
            "testUser",
            "test@test.com",
            "encodedPassword",
            "USER",
            now,
            now,
        )
    }

    private fun insertTestPostData(post: Post) {
        val sql = """
            INSERT INTO post_db.post (id, user_id, title, content, view_count, like_count, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            post.id,
            post.userId,
            post.title,
            post.content,
            post.viewCount,
            post.likeCount,
            post.createdAt,
            post.createdAt,
        )
    }
}