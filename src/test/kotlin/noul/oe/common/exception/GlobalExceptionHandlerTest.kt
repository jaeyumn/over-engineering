package noul.oe.common.exception

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("BaseException 발생 시, handleBaseException이 핸들링된다")
    fun test100() {
        val errorCode = CommonErrorCode.INVALID_INPUT.code
        val errorMessage = CommonErrorCode.INVALID_INPUT.message

        mockMvc.perform(
            get("/api/test/base-exception")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value(errorCode))
            .andExpect(jsonPath("$.error.message").value(errorMessage))
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
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
            .andExpect(jsonPath("$.error.message").value(containsString("이름은 필수입니다")))
            .andExpect(jsonPath("$.error.message").value(containsString("나이는 1 이상이어야 합니다")))
    }

    @Test
    @DisplayName("내부 서버 오류 시, handleException이 핸들링된다")
    fun test102() {
        mockMvc.perform(
            get("/api/test/internal-exception")
        )
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("INTERNAL_SERVER_ERROR"))
            .andExpect(jsonPath("$.error.message").value(containsString("테스트 오류 발생")))
    }
}