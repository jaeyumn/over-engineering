package noul.oe.user.exception

import noul.oe.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "Username already exists.", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found.", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid username or password", HttpStatus.UNAUTHORIZED),
}