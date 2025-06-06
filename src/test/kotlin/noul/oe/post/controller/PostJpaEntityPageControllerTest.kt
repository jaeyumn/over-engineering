package noul.oe.post.controller

import noul.oe.core.comment.service.CommentService
import noul.oe.config.SecurityTestConfig
import noul.oe.core.post.adapter.`in`.web.page.PostPageController
import noul.oe.core.post.application.port.input.PostPageResponse
import noul.oe.core.user1.service.UserService
import noul.oe.support.security.UserPrincipal
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(PostPageController::class)
@Import(SecurityTestConfig::class)
class PostJpaEntityPageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    @MockBean
    private lateinit var postService: PostService

    @MockBean
    private lateinit var commentService: CommentService

    private val postId = 1L
    private val username = "testuser"
    private val userId = "testId"
    private val user = UserPrincipal(
        userId = userId,
        username = username,
        "",
        authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
    )

    @Test
    @DisplayName("게시글 목록 페이지가 정상 렌더링된다")
    @WithMockUser(username = "testuser")
    fun readAllTest() {
        // given
        val page = PageImpl(emptyList<PostPageResponse>())
        whenever(postService.readAll(any())).thenReturn(page)

        // when & then
        mockMvc.perform(get("/posts").param("page", "0"))
            .andExpect(status().isOk)
            .andExpect(view().name("post-list"))
            .andExpect(model().attributeExists("posts"))
            .andExpect(model().attribute("username", username))
    }

    @Test
    @DisplayName("게시글 상세 페이지가 정상 렌더링된다")
    @WithMockUser(username = "testuser")
    fun readTest() {
        // given
        val post = PostDetailResponse(
            postId = postId,
            userId = userId,
            username = username,
            title = "title",
            content = "content",
            viewCount = 10,
            likeCount = 1,
            commentCount = 1,
            liked = false,
            createdAt = LocalDateTime.parse("2025-05-01T23:53:25"),
            editable = true
        )
        val comments = listOf<CommentResponse>()

        whenever(userService.getUserIdByUsername(username)).thenReturn(userId)
        whenever(postService.read(postId, user)).thenReturn(post)
        whenever(commentService.readAll(postId, userId)).thenReturn(comments)

        // when & then
        mockMvc.perform(get("/posts/{postId}", postId))
            .andExpect(status().isOk)
            .andExpect(view().name("post-detail"))
            .andExpect(model().attributeExists("post"))
            .andExpect(model().attributeExists("comments"))
            .andExpect(model().attribute("username", username))
    }

    @Nested
    @DisplayName("게시글 수정 페이지 접근")
    inner class EditPageTest() {
        @Test
        @DisplayName("작성자가 아니면 403 예외가 발생한다")
        @WithMockUser(username = "testuser")
        fun test1() {
            // given
            val post = PostDetailResponse(
                postId = postId,
                userId = userId,
                username = username,
                title = "title",
                content = "content",
                viewCount = 10,
                likeCount = 1,
                commentCount = 1,
                liked = false,
                createdAt = LocalDateTime.parse("2025-04-30T23:53:25"),
                editable = false
            )

            whenever(userService.getUserIdByUsername(username)).thenReturn(userId)
            whenever(postService.read(postId, user)).thenReturn(post)

            // when & then
            mockMvc.perform(get("/posts/{postId}/edit", postId))
                .andExpect(status().isForbidden)
        }

        @Test
        @DisplayName("작성자 본인이면 수정 페이지가 정상 렌더링된다")
        @WithMockUser(username = "testuser")
        fun test100() {
            // given
            val post = PostDetailResponse(
                postId = postId,
                userId = userId,
                username = username,
                title = "title",
                content = "content",
                viewCount = 10,
                likeCount = 1,
                commentCount = 1,
                liked = false,
                createdAt = LocalDateTime.parse("2025-04-30T23:53:25"),
                editable = true
            )

            whenever(userService.getUserIdByUsername(username)).thenReturn(userId)
            whenever(postService.read(postId, user)).thenReturn(post)

            // when & then
            mockMvc.perform(get("/posts/{postId}/edit", postId))
                .andExpect(status().isOk)
                .andExpect(view().name("post-edit"))
                .andExpect(model().attributeExists("post"))
        }
    }
}