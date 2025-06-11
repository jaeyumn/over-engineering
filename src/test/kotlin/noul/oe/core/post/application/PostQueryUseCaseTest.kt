package noul.oe.core.post.application

import noul.oe.core.post.application.exception.PostNotFoundException
import noul.oe.core.post.application.port.output.PostLikeRepositoryPort
import noul.oe.core.post.application.port.output.PostRepositoryPort
import noul.oe.core.post.application.usecase.PostQueryUseCase
import noul.oe.core.post.domain.Post
import noul.oe.support.port.CommentInfoProvider
import noul.oe.support.port.UserInfoProvider
import noul.oe.support.port.dto.CommentInfo
import noul.oe.util.WithMockCustomUser
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class PostQueryUseCaseTest {

    private lateinit var postRepositoryPort: PostRepositoryPort
    private lateinit var postLikeRepositoryPort: PostLikeRepositoryPort
    private lateinit var userInfoProvider: UserInfoProvider
    private lateinit var commentInfoProvider: CommentInfoProvider
    private lateinit var sut: PostQueryUseCase

    private val userId = "testId"
    private val otherUserId = "otherUserId"
    private val postId = 1L
    private val noneExistsPostId = 1000L
    private val now = LocalDateTime.now()

    private val testPost = Post(
        id = postId,
        userId = userId,
        title = "title",
        content = "content",
        createdAt = now,
        viewCount = 0,
        likeCount = 0
    )

    private val testComment = CommentInfo(
        id = 1L,
        userId = otherUserId,
        username = "otherUsername",
        content = "content",
        createdAt = now.toString(),
        editable = false
    )

    @BeforeEach
    fun setUp() {
        postRepositoryPort = mock()
        postLikeRepositoryPort = mock()
        userInfoProvider = mock()
        commentInfoProvider = mock()
        sut = PostQueryUseCase(postRepositoryPort, postLikeRepositoryPort, userInfoProvider, commentInfoProvider)
    }

    @Nested
    @WithMockCustomUser
    inner class ReadDetail {

        @Test
        @DisplayName("존재하지 않는 게시글 상세 조회 시 예외가 발생한다")
        fun test1() {
            // given
            whenever(postRepositoryPort.findById(noneExistsPostId))
                .thenReturn(null)

            // when & then
            assertThatThrownBy { sut.readDetail(noneExistsPostId) }
                .isInstanceOf(PostNotFoundException::class.java)
        }

        @Test
        @DisplayName("게시글 상세 조회에 성공한다")
        fun test100() {
            // given
            val commentCount = 5
            whenever(postRepositoryPort.findById(postId)).thenReturn(testPost)
            whenever(commentInfoProvider.getCommentCount(postId)).thenReturn(commentCount)
            whenever(postLikeRepositoryPort.existsByUserIdAndPostId(userId, postId)).thenReturn(true)

            // when
            val result = sut.readDetail(postId)

            // then
            assertThat(result.postId).isEqualTo(postId)
            assertThat(result.title).isEqualTo("title")
            assertThat(result.content).isEqualTo("content")
            assertThat(result.viewCount).isEqualTo(1) // 조회수 증가
            assertThat(result.likeCount).isEqualTo(0)
            assertThat(result.commentCount).isEqualTo(5)
            assertThat(result.liked).isTrue()
            assertThat(result.editable).isTrue()
            verify(postRepositoryPort).findById(postId)
            verify(commentInfoProvider).getCommentCount(postId)
            verify(postLikeRepositoryPort).existsByUserIdAndPostId(userId, postId)
        }
    }

    @Nested
    @WithMockCustomUser
    inner class ReadAll {

        @Test
        @DisplayName("게시글 전체 조회에 성공한다")
        fun test100() {
            // given
            val pageable = PageRequest.of(0, 10)
            val posts = listOf(
                testPost.copy(id = 10L, userId = userId, title = "title1"),
                testPost.copy(id = 20L, userId = otherUserId, title = "title2"),
            )
            val postPage = PageImpl(posts, pageable, posts.size.toLong())

            whenever(postRepositoryPort.findAll(pageable)).thenReturn(postPage)
            whenever(userInfoProvider.getUsername(userId)).thenReturn("testUsername")
            whenever(userInfoProvider.getUsername(otherUserId)).thenReturn("otherUsername")
            whenever(commentInfoProvider.getCommentCount(10L)).thenReturn(3)
            whenever(commentInfoProvider.getCommentCount(20L)).thenReturn(0)

            // when
            val result = sut.readAll(pageable)

            // then
            assertThat(result).hasSize(2)
            assertThat(result.content[0].postId).isEqualTo(10L)
            assertThat(result.content[0].username).isEqualTo("testUsername")
            assertThat(result.content[0].commentCount).isEqualTo(3)
            assertThat(result.content[1].postId).isEqualTo(20L)
            assertThat(result.content[1].username).isEqualTo("otherUsername")
            assertThat(result.content[1].commentCount).isEqualTo(0)
            verify(postRepositoryPort).findAll(pageable)
            verify(userInfoProvider, times(2)).getUsername(any())
            verify(commentInfoProvider, times(2)).getCommentCount(any())
        }
    }

    @Nested
    @WithMockCustomUser
    inner class ReadWithComments {

        @Test
        @DisplayName("존재하지 않는 게시글과 댓글 조회 시 예외가 발생한다")
        fun test1() {
            // given
            whenever(postRepositoryPort.findById(noneExistsPostId)).thenReturn(null)

            // when & then
            assertThatThrownBy { sut.readWithComments(noneExistsPostId) }
                .isInstanceOf(PostNotFoundException::class.java)
        }

        @Test
        @DisplayName("게시글과 댓글 조회에 성공한다")
        fun test100() {
            // given
            val comments = listOf(
                testComment.copy(id = 10L, userId = userId),
                testComment.copy(id = 20L, userId = otherUserId)
            )

            whenever(postRepositoryPort.findById(postId)).thenReturn(testPost)
            whenever(commentInfoProvider.getCommentCount(postId)).thenReturn(2)
            whenever(postLikeRepositoryPort.existsByUserIdAndPostId(userId, postId)).thenReturn(false)
            whenever(commentInfoProvider.getCommentList(postId, userId)).thenReturn(comments)
            whenever(userInfoProvider.getUsername(userId)).thenReturn("testUsername")
            whenever(userInfoProvider.getUsername(otherUserId)).thenReturn("otherUsername")


            // when
            val result = sut.readWithComments(postId)

            // then
            assertThat(result.post.postId).isEqualTo(postId)
            assertThat(result.post.title).isEqualTo("title")
            assertThat(result.post.content).isEqualTo("content")
            assertThat(result.post.viewCount).isEqualTo(0)
            assertThat(result.post.likeCount).isEqualTo(0)
            assertThat(result.post.commentCount).isEqualTo(2)
            assertThat(result.post.liked).isFalse()
            assertThat(result.post.editable).isTrue()

            assertThat(result.comments).hasSize(2)
            assertThat(result.comments[0].id).isEqualTo(10L)
            assertThat(result.comments[0].username).isEqualTo("testUsername")
            assertThat(result.comments[1].id).isEqualTo(20L)
            assertThat(result.comments[1].username).isEqualTo("otherUsername")

            verify(postRepositoryPort).findById(postId)
            verify(commentInfoProvider).getCommentCount(postId)
            verify(postLikeRepositoryPort).existsByUserIdAndPostId(userId, postId)
            verify(commentInfoProvider).getCommentList(postId, userId)
            verify(userInfoProvider, times(2)).getUsername(any())
        }
    }
}