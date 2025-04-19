package noul.oe.comment.controller

import com.fasterxml.jackson.databind.ObjectMapper
import noul.oe.comment.dto.request.CommentCreateRequest
import noul.oe.comment.dto.request.CommentModifyRequest
import noul.oe.comment.dto.response.CommentResponse
import noul.oe.comment.service.CommentService
import noul.oe.config.SecurityTestConfig
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
import java.time.LocalDateTime

@WebMvcTest(CommentController::class)
@Import(SecurityTestConfig::class)
class CommentControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var commentService: CommentService

    private val objectMapper = ObjectMapper()
    private val userId = "testuser"
    private val postId = 1L
    private val commentId = 10L

    @Test
    @WithMockUser(username = "testuser")
    fun createTest() {
        // given
        val request = CommentCreateRequest("댓글입니다")
        val response = CommentResponse(
            id = commentId,
            content = request.content,
            userId = userId,
            createdAt = LocalDateTime.now(),
            children = emptyList()
        )
        whenever(commentService.create(eq(postId), eq(userId), any())).thenReturn(response)

        // when & then
        mockMvc.perform(
            post("/api/posts/{postId}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").value("댓글입니다"))

        verify(commentService).create(eq(postId), eq(userId), any())
    }

    @Test
    fun readAllTest() {
        // given
        val response = listOf(
            CommentResponse(
                id = 1L,
                content = "댓글1",
                userId = userId,
                createdAt = LocalDateTime.now(),
                children = listOf(
                    CommentResponse(
                        id = 2L,
                        content = "대댓글1",
                        userId = userId,
                        createdAt = LocalDateTime.now(),
                        children = emptyList()
                    )
                )
            )
        )
        whenever(commentService.readAll(postId)).thenReturn(response)

        // when & then
        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data[0].content").value("댓글1"))
            .andExpect(jsonPath("$.data[0].children[0].content").value("대댓글1"))
    }

    @Test
    @WithMockUser(username = "testuser")
    fun modifyTest() {
        // given
        val request = CommentModifyRequest("수정된 내용")

        // when & then
        mockMvc.perform(
            put("/api/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        verify(commentService).modify(eq(commentId), eq(userId), eq("수정된 내용"))
    }

    @Test
    @WithMockUser(username = "testuser")
    fun deleteTest() {
        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}", commentId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        verify(commentService).remove(eq(commentId), eq(userId))
    }
}