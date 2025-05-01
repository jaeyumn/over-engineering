package noul.oe.support.exception

import org.springframework.http.HttpStatus

data class ErrorResponse(
    val code: String,
    val message: String,
)

enum class CommonErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    INVALID_INPUT("INVALID_INPUT", "유효하지 않은 값입니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버에서 오류가 발생하였습니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR),
}