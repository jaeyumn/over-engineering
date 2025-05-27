package noul.oe.domain.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import noul.oe.config.SecurityTestConfig
import noul.oe.domain.user.controller.AuthApiController
import noul.oe.domain.user.dto.request.UserLogInRequest
import noul.oe.domain.user.dto.response.UserResponse
import noul.oe.domain.user.service.AuthService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(AuthApiController::class)
@Import(SecurityTestConfig::class)
class AuthApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var authService: AuthService

    private val objectMapper = ObjectMapper()

    @Test
    fun loginTest() {
        // given
        val request = UserLogInRequest("testuser", "password123")
        val response = UserResponse(
            id = UUID.randomUUID().toString(),
            username = request.username,
            email = "test@test.com",
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now(),
        )
        val httpRequest = MockHttpServletRequest()

        whenever(authService.login(request, httpRequest)).thenReturn(response)

        // when & then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun logoutTest() {
        // when & then
        mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isNoContent)
    }
}