package noul.oe.post.controller

import com.fasterxml.jackson.databind.ObjectMapper
import noul.oe.config.SecurityTestConfig
import noul.oe.post.dto.request.PostCreateRequest
import noul.oe.post.dto.request.PostModifyRequest
import noul.oe.post.dto.response.PostDetailResponse
import noul.oe.post.dto.response.PostPageResponse
import noul.oe.post.service.PostService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PostController::class)
@Import(SecurityTestConfig::class)
class PostControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var postService: PostService

    private val objectMapper = ObjectMapper()
    private val userId = "testuser"
    private val postId = 1L

    @Test
    @WithMockUser(username = "testuser")
    fun createTest() {
        // given
        val request = PostCreateRequest("title", "content")

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
    fun readTest() {
        // given
        val response = PostDetailResponse(postId, userId, "title", "content", 10L, 2L)
        whenever(postService.read(postId)).thenReturn(response)

        // when & then
        mockMvc.perform(get("/api/posts/${postId}", postId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.title").value("title"))
    }

    @Test
    fun readAllTest() {
        val response = PageImpl(listOf(PostPageResponse(postId, "title")))
        whenever(postService.readAll(any())).thenReturn(response)

        mockMvc.perform(get("/api/posts"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray)
            .andExpect(jsonPath("$.data.content[0].title").value("title"))
    }

    @Test
    @WithMockUser(username = "testuser")
    fun modifyTest() {
        val request = PostModifyRequest("new title", "new content")

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
        mockMvc.perform(delete("/api/posts/{postId}", postId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        verify(postService).remove(userId, postId)
    }

    @Test
    @WithMockUser(username = "testuser")
    fun likePost_success() {
        mockMvc.perform(post("/api/posts/{postId}/like", postId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        verify(postService).like(userId, postId)
    }

    @Test
    @WithMockUser(username = "testuser")
    fun unlikePost_success() {
        mockMvc.perform(delete("/api/posts/{postId}/like", postId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        verify(postService).unlike(userId, postId)
    }
}