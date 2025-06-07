package noul.oe.core.user.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import noul.oe.config.SecurityTestConfig
import noul.oe.core.user.adapter.`in`.web.api.UserLoginRequest
import noul.oe.core.user.adapter.out.session.SessionHandler
import noul.oe.core.user.application.port.input.AuthCommandPort
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
class AuthApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var authCommandPort: AuthCommandPort

    @MockBean
    lateinit var sessionHandler: SessionHandler

    @Test
    @DisplayName("로그인 성공 시 204 응답을 반환한다")
    fun loginTest() {
        // given
        val request = UserLoginRequest("testUser", "password")

        doNothing().whenever(authCommandPort).login(any())
        doNothing().whenever(sessionHandler).setUpSecurityContext(any(), any(), any())

        // when & then
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @DisplayName("로그아웃 성공 시 204 응답을 반환한다")
    fun logoutTest() {
        // given
        doNothing().whenever(sessionHandler).logout(any<HttpServletRequest>())

        // when & then
        mockMvc.post("/api/auth/logout")
            .andExpect {
                status { isNoContent() }
            }
    }
}