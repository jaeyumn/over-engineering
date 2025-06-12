package noul.oe.core.post.adapter

import noul.oe.core.post.adapter.out.PostJpaRepository
import noul.oe.core.post.adapter.out.PostLikeJpaRepository
import noul.oe.core.post.adapter.out.PostLikeRepositoryAdapter
import noul.oe.core.post.domain.PostLike
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@Import(PostLikeRepositoryAdapter::class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class PostLikeRepositoryAdapterTest {

    @Autowired
    lateinit var sut: PostLikeRepositoryAdapter

    @Autowired
    lateinit var postLikeJpaRepository: PostLikeJpaRepository

    @Autowired
    lateinit var postJpaRepository: PostJpaRepository

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var testUserId: String
    private val testPostId: Long = 1L
    private val testPostLikeId: Long = 1L
    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        testUserId = UUID.randomUUID().toString()

        // 초기화
        postLikeJpaRepository.deleteAll()
        postJpaRepository.deleteAll()
        jdbcTemplate.update("DELETE FROM users")

        // 테스트 데이터 삽입
        insertTestUserData()
        insertTestPostData()
    }

    @Test
    fun saveTest() {
        // given
        val postLike = PostLike(testPostLikeId, testUserId, testPostId)

        // when
        sut.save(postLike)

        // then
        val saved = postLikeJpaRepository.findById(postLike.id!!).orElse(null)
        assertThat(saved).isNotNull
        assertThat(saved.userId).isEqualTo(postLike.userId)
        assertThat(saved.postId).isEqualTo(postLike.postId)
    }

    @Test
    fun findByUserIdAndPostIdTest() {
        // given
        insertTestPostLikeData()

        // when
        val postLike = sut.findByUserIdAndPostId(testUserId, testPostId)

        // then

        // then
        assertThat(postLike).isNotNull
        assertThat(postLike!!.id).isEqualTo(testPostLikeId)
        assertThat(postLike.userId).isEqualTo(testUserId)
        assertThat(postLike.postId).isEqualTo(testPostId)
    }

    @Test
    fun existsByUserIdAndPostIdTest() {
        // given
        insertTestPostLikeData()

        // when
        val exists = sut.existsByUserIdAndPostId(testUserId, testPostId)

        // then
        assertThat(exists).isTrue
    }

    @Test
    fun deleteTest() {
        // given
        insertTestPostLikeData()

        // when
        sut.delete(testPostLikeId)

        // then
        assertThat(postLikeJpaRepository.findById(testPostLikeId).isPresent).isFalse()
    }

    @Test
    fun deleteAllByPostIdTest() {
        // given
        insertTestPostLikeData()

        // when
        sut.deleteAllByPostId(testPostId)

        // then
        assertThat(postLikeJpaRepository.findById(testPostLikeId).isPresent).isFalse()
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

    private fun insertTestPostData() {
        val sql = """
            INSERT INTO post_db.post (id, user_id, title, content, view_count, like_count, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            testPostId,
            testUserId,
            "title",
            "content",
            5L,
            1L,
            now,
            now,
        )
    }

    private fun insertTestPostLikeData() {
        val sql = """
            INSERT INTO post_db.post_like (id, user_id, post_id, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            testPostLikeId,
            testUserId,
            testPostId,
            now,
            now,
        )
    }
}