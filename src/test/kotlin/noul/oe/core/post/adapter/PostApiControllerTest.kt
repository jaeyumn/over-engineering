package noul.oe.core.post.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import noul.oe.config.SecurityTestConfig
import noul.oe.core.post.adapter.`in`.web.api.PostCreateRequest
import noul.oe.core.post.adapter.`in`.web.api.PostModifyRequest
import noul.oe.core.post.application.port.input.PostCommandPort
import noul.oe.core.post.application.port.input.PostLikeCommandPort
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
class PostApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var postCommandPort: PostCommandPort

    @MockBean
    lateinit var postLikeCommandPort: PostLikeCommandPort

    private val postId = 1L

    @Test
    @DisplayName("POST /api/posts - 게시글 생성 성공 시 201 응답을 반환한다")
    fun createTest() {
        // given
        val request = PostCreateRequest("title", "content")

        doNothing().whenever(postCommandPort).create(any())

        // when & then
        mockMvc.post("/api/posts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
            }
    }

    @Test
    @DisplayName("PUT /api/posts/{postId} - 게시글 수정 성공 시 204 응답을 반환한다")
    fun modifyTest() {
        // given
        val request = PostModifyRequest("modified title", "modified content")

        doNothing().whenever(postCommandPort).modify(any())

        // when & then
        mockMvc.put("/api/posts/{postId}", postId) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @DisplayName("DELETE /api/posts/{postId} - 게시글 삭제 성공 시 204 응답을 반환한다")
    fun removeTest() {
        // given
        doNothing().whenever(postCommandPort).remove(any())

        // when & then
        mockMvc.delete("/api/posts/{postId}", postId)
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @DisplayName("POST /api/posts/{postId}/like - 게시글 좋아요 성공 시 200 응답을 반환한다")
    fun likeTest() {
        // given
        doNothing().whenever(postLikeCommandPort).like(any())

        // when & then
        mockMvc.post("/api/posts/{postId}/like", postId)
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @DisplayName("DELETE /api/posts/{postId}/like - 게시글 좋아요 취소 성공 시 200 응답을 반환한다")
    fun unlikeTest() {
        // given
        doNothing().whenever(postLikeCommandPort).unlike(any())

        // when & then
        mockMvc.delete("/api/posts/{postId}/like", postId)
            .andExpect {
                status { isNoContent() }
            }
    }
}