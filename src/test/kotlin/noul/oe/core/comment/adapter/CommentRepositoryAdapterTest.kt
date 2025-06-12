package noul.oe.core.comment.adapter

import noul.oe.core.comment.adapter.out.persistence.CommentJpaRepository
import noul.oe.core.comment.adapter.out.persistence.CommentRepositoryAdapter
import noul.oe.core.comment.application.exception.CommentNotFoundException
import noul.oe.core.comment.domain.Comment
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
@Import(CommentRepositoryAdapter::class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class CommentRepositoryAdapterTest {

    @Autowired
    lateinit var sut: CommentRepositoryAdapter

    @Autowired
    lateinit var commentJpaRepository: CommentJpaRepository

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var testUserId: String
    private val testPostId = 100L
    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        testUserId = UUID.randomUUID().toString()

        // 초기화
        commentJpaRepository.deleteAll()
        jdbcTemplate.update("DELETE FROM comment_db.comment")
        jdbcTemplate.update("DELETE FROM users")
        jdbcTemplate.update("DELETE FROM post_db.post")

        // 테스트 데이터 삽입
        insertTestUserData()
        insertTestPostData()
    }

    @Test
    fun saveTest() {
        // given
        val comment = createComment(id = 1L)

        // when
        sut.save(comment)

        // then
        val savedEntity = commentJpaRepository.findById(comment.id).orElse(null)
        assertThat(savedEntity).isNotNull
        assertThat(savedEntity.content).isEqualTo(comment.content)
        assertThat(savedEntity.postId).isEqualTo(comment.postId)
        assertThat(savedEntity.userId).isEqualTo(comment.userId)
        assertThat(savedEntity.parentId).isEqualTo(comment.parentId)
    }

    @Test
    fun findByIdTest() {
        // given
        val comment = createComment(id = 2L)
        insertTestCommentData(comment)

        // when
        val foundComment = sut.findById(comment.id)

        // then
        assertThat(foundComment).isNotNull
        assertThat(foundComment.id).isEqualTo(comment.id)
        assertThat(foundComment.content).isEqualTo(comment.content)
    }

    @Test
    fun findByIdNotFoundTest() {
        // given
        val nonExistentId = 999L

        // when & then
        assertThatThrownBy { sut.findById(nonExistentId) }
            .isInstanceOf(CommentNotFoundException::class.java)
    }

    @Test
    fun findAllByParentIdTest() {
        // given
        val parentComment = createComment(id = 3L)
        insertTestCommentData(parentComment)

        val reply1 = createComment(id = 4L, parentId = parentComment.id)
        val reply2 = createComment(id = 5L, parentId = parentComment.id)
        insertTestCommentData(reply1)
        insertTestCommentData(reply2)

        // when
        val replies = sut.findAllByParentId(parentComment.id)

        // then
        assertThat(replies).hasSize(2)
        assertThat(replies.map { it.id }).containsExactlyInAnyOrder(reply1.id, reply2.id)
        assertThat(replies.all { it.parentId == parentComment.id }).isTrue()
    }

    @Test
    fun deleteByIdTest() {
        // given
        val commentToDelete = createComment(id = 6L)
        sut.save(commentToDelete)

        // when
        sut.delete(commentToDelete.id)

        // then
        assertThat(commentJpaRepository.findById(commentToDelete.id).isPresent).isFalse()
    }

    @Test
    fun deleteAllTest() {
        // given
        val comment1 = createComment(id = 7L)
        val comment2 = createComment(id = 8L)
        sut.save(comment1)
        sut.save(comment2)

        val commentsToDelete = listOf(comment1, comment2)

        // when
        sut.deleteAll(commentsToDelete)

        // then
        assertThat(commentJpaRepository.findById(comment1.id).isPresent).isFalse()
        assertThat(commentJpaRepository.findById(comment2.id).isPresent).isFalse()
    }

    private fun insertTestUserData(userId: String = testUserId) {
        val sql = """
            INSERT INTO users(id, username, email, password, role, created_at, modified_at)
            VALUES(?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            userId,
            "testUser",
            "test@test.com",
            "encodedPassword",
            "USER",
            now,
            now,
        )
    }

    private fun insertTestPostData(
        postId: Long = testPostId,
        userId: String = testUserId,
        title: String = "Test Post",
        content: String = "Test Content"
    ) {
        val sql = """
            INSERT INTO post_db.post (id, user_id, title, content, view_count, like_count, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            postId,
            userId,
            title,
            content,
            0,
            0,
            now,
            now,
        )
    }

    private fun insertTestCommentData(comment: Comment) {
        val sql = """
        INSERT INTO comment_db.comment (id, content, post_id, user_id, parent_id, created_at, modified_at)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """.trimIndent()

        jdbcTemplate.update(
            sql,
            comment.id,
            comment.content,
            comment.postId,
            comment.userId,
            comment.parentId,
            comment.createdAt,
            comment.modifiedAt
        )
    }

    private fun createComment(
        id: Long,
        content: String = "Test comment",
        postId: Long = testPostId,
        userId: String = testUserId,
        parentId: Long? = null
    ): Comment {
        return Comment(
            id = id,
            content = content,
            postId = postId,
            userId = userId,
            parentId = parentId,
            createdAt = now,
            modifiedAt = now
        )
    }
}