package noul.oe.post.controller

import com.fasterxml.jackson.databind.ObjectMapper
import noul.oe.config.SecurityTestConfig
import noul.oe.post.dto.request.PostCreateRequest
import noul.oe.post.dto.request.PostModifyRequest
import noul.oe.post.service.PostService
import noul.oe.user.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PostApiController::class)
@Import(SecurityTestConfig::class)
class PostApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var postService: PostService

    @MockBean
    private lateinit var userService: UserService

    private val objectMapper = ObjectMapper()
    private val userId = "testId"
    private val username = "testuser"
    private val postId = 1L

    @Test
    @WithMockUser(username = "testuser")
    fun createTest() {
        // given
        val request = PostCreateRequest("title", "content")

        whenever(userService.getUserIdByUsername(username)).thenReturn(userId)

        // when & then
        mockMvc.perform(
            post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))

        verify(postService).create(eq(userId), any())
    }

    @Test
    @WithMockUser(username = "testuser")
    fun modifyTest() {
        // given
        val request = PostModifyRequest("new title", "new content")

        whenever(userService.getUserIdByUsername(username)).thenReturn(userId)

        // when & then
        mockMvc.perform(
            put("/api/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        verify(postService).modify(eq(userId), eq(postId), any())
    }

    @Test
    @WithMockUser(username = "testuser")
    fun deleteTest() {
        // given
        whenever(userService.getUserIdByUsername(username)).thenReturn(userId)

        // when & then
        mockMvc.perform(delete("/api/posts/{postId}", postId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        verify(postService).remove(userId, postId)
    }

    @Test
    @WithMockUser(username = "testuser")
    fun likePost_success() {
        // given
        whenever(userService.getUserIdByUsername(username)).thenReturn(userId)

        // when & then
        mockMvc.perform(post("/api/posts/{postId}/like", postId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        verify(postService).like(userId, postId)
    }

    @Test
    @WithMockUser(username = "testuser")
    fun unlikePost_success() {
        // given
        whenever(userService.getUserIdByUsername(username)).thenReturn(userId)

        // when & then
        mockMvc.perform(delete("/api/posts/{postId}/like", postId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        verify(postService).unlike(userId, postId)
    }
}