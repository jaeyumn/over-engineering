package noul.oe.support.exception

import noul.oe.config.SecurityTestConfig
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityTestConfig::class)
class GlobalExceptionHandlerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("BaseException 발생 시, handleBaseException이 핸들링된다")
    fun test100() {
        val errorCode = CommonErrorCode.INVALID_INPUT.code
        val errorMessage = CommonErrorCode.INVALID_INPUT.message

        mockMvc.perform(get("/api/test/base-exception"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(errorCode))
            .andExpect(jsonPath("$.message").value(errorMessage))
    }

    @Test
    @DisplayName("Validation 검증 실패 시, handleValidationException이 핸들링된다")
    fun test101() {
        mockMvc.perform(
            post("/api/test/validation-exception")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "", "age": 0}""")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
            .andExpect(jsonPath("$.message").value(containsString("이름은 필수입니다")))
            .andExpect(jsonPath("$.message").value(containsString("나이는 1 이상이어야 합니다")))
    }

    @Test
    @DisplayName("내부 서버 오류 시, handleException이 핸들링된다")
    fun test102() {
        mockMvc.perform(get("/api/test/internal-exception"))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
            .andExpect(jsonPath("$.message").value(containsString("테스트 오류 발생")))
    }

    @Test
    @DisplayName("로그인 실패 시, handleBadCredentialsException이 핸들링된다")
    fun test103() {
        mockMvc.perform(get("/api/test/credentials-exception"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
            .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않음"))
    }
}