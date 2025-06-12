package noul.oe.core.comment.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import noul.oe.config.SecurityTestConfig
import noul.oe.core.comment.adapter.`in`.web.CommentCreateRequest
import noul.oe.core.comment.adapter.`in`.web.CommentModifyRequest
import noul.oe.core.comment.application.port.input.CommentCommandPort
import noul.oe.util.WithMockCustomUser
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityTestConfig::class)
class CommentApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var commentCommandPort: CommentCommandPort

    private val postId = 1L
    private val commentId = 10L

    @Test
    @DisplayName("POST /api/posts/{postId}/comments - 댓글 생성 성공 시 201 응답을 반환한다")
    @WithMockCustomUser
    fun createCommentTest() {
        // given
        val request = CommentCreateRequest("new content")

        doNothing().whenever(commentCommandPort).create(any())

        // when & then
        mockMvc.post("/api/posts/{postId}/comments", postId) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
            }
    }

    @Test
    @DisplayName("POST /api/comments/{commentId}/replies - 답글 생성 성공 시 201 응답을 반환한다")
    fun replyCommentTest() {
        // given
        val request = CommentCreateRequest("new content")

        doNothing().whenever(commentCommandPort).reply(any())

        // when & then
        mockMvc.post("/api/comments/{commentId}/replies", commentId) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
            }
    }

    @Test
    @DisplayName("PUT /api/comments/{commentId} - 댓글 수정 성공 시 204 응답을 반환한다")
    fun modifyCommentTest() {
        // given
        val request = CommentModifyRequest("new content")

        doNothing().whenever(commentCommandPort).modify(any())

        // when & then
        mockMvc.put("/api/comments/{commentId}", commentId) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @DisplayName("DELETE /api/comments/{commentId} - 댓글 삭제 성공 시 204 응답을 반환한다")
    fun removeCommentTest() {
        // given
        doNothing().whenever(commentCommandPort).remove(any())

        // when & then
        mockMvc.delete("/api/comments/{commentId}", commentId)
            .andExpect {
                status { isNoContent() }
            }
    }
}