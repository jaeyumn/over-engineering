package noul.oe.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import noul.oe.config.SecurityTestConfig
import noul.oe.user.dto.request.UserSignUpRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
@Import(SecurityTestConfig::class)
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

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
}