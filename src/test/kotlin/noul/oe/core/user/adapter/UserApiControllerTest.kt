package noul.oe.core.user.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import noul.oe.config.SecurityTestConfig
import noul.oe.core.user.adapter.`in`.web.api.UserSignUpRequest
import noul.oe.core.user.application.port.input.UserCommandPort
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
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityTestConfig::class)
class UserApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var userCommandPort: UserCommandPort

    @Test
    @DisplayName("회원사입 성공 시 201 응답을 반환한다")
    fun signUpTest() {
        // given
        val request = UserSignUpRequest("testUser", "test@test.com", "password")

        doNothing().whenever(userCommandPort).signUp(any())

        // when & then
        mockMvc.post("/api/users/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
            }
    }
}