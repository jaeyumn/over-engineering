package noul.oe.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import noul.oe.common.response.ApiResponse
import noul.oe.config.SecurityTestConfig
import noul.oe.user.dto.request.UserLogInRequest
import noul.oe.user.dto.request.UserSignUpRequest
import noul.oe.user.dto.response.UserResponse
import noul.oe.user.service.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.UUID

@WebMvcTest(UserController::class)
@Import(SecurityTestConfig::class)
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    private val objectMapper = ObjectMapper()

    @Test
    @DisplayName("회원가입 성공")
    fun signUpTest() {
        // given
        val request = UserSignUpRequest("testuser", "test@test.com", "password123")

        // when & then
        mockMvc.perform(
            post("/api/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    @Test
    @DisplayName("로그인 성공")
    fun logInTest() {
        // given
        val request = UserLogInRequest("testuser", "password123")
        val response = UserResponse(
            id = UUID.randomUUID().toString(),
            username = request.username,
            email = "test@test.com",
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now(),
        )

        whenever(userService.logIn(request)).thenReturn(response)

        // when & then
        mockMvc.perform(
            post("/api/users/log-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.username").value(request.username))
    }
}