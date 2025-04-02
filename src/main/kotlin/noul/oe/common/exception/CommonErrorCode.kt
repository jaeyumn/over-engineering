package noul.oe.common.exception

import org.springframework.http.HttpStatus

enum class CommonErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    INVALID_INPUT("INVALID_INPUT", "Invalid input provided.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR),
}