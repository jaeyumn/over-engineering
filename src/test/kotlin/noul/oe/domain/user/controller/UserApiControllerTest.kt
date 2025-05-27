package noul.oe.domain.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import noul.oe.config.SecurityTestConfig
import noul.oe.domain.user.controller.UserApiController
import noul.oe.domain.user.dto.request.UserSignUpRequest
import noul.oe.domain.user.service.UserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserApiController::class)
@Import(SecurityTestConfig::class)
class UserApiControllerTest {
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
        doNothing().whenever(userService).signUp(any())

        // when & then
        mockMvc.perform(
            post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
    }
}