package noul.oe.common.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class BaseExceptionTest {

    @Test
    @DisplayName("BaseException은 ErrorCode의 message가 존재하면 매핑된다")
    fun test100() {
        // given
        val errorCode = CommonErrorCode.INVALID_INPUT

        // when
        val exception = BaseException(errorCode)

        // then
        assertThat(exception.message).isEqualTo(errorCode.message)
        assertThat(exception.errorCode).isEqualTo(errorCode)
    }

    @Test
    @DisplayName("BaseException은 custom message를 사용할 수 있다")
    fun test101() {
        // given
        val errorCode = CommonErrorCode.INVALID_INPUT
        val customMessage = "custom error message"

        // when
        val exception = BaseException(errorCode, customMessage)

        // then
        assertThat(exception.message).isEqualTo(customMessage)
        assertThat(exception.errorCode).isEqualTo(errorCode)
    }

    @Test
    @DisplayName("BaseException은 HTTP 상태 코드를 ErrorCode에서 가져온다")
    fun test102() {
        // given
        val errorCode = CommonErrorCode.INVALID_INPUT

        // when
        val exception = BaseException(errorCode)

        // then
        assertThat(exception.errorCode.status).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}
