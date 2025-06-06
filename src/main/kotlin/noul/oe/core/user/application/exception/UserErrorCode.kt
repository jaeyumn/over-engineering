package noul.oe.core.user.application.exception

import noul.oe.support.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*


enum class UserErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : ErrorCode {
    USERNAME_ALREADY_EXISTS(CONFLICT, "USERNAME_ALREADY_EXISTS", "이미 존재하는 이름입니다."),
    EMAIL_ALREADY_EXISTS(CONFLICT, "EMAIL_ALREADY_EXISTS", "이미 존재하는 이메일입니다."),
    USER_NOT_FOUND(NOT_FOUND, "USER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    INVALID_CREDENTIALS(UNAUTHORIZED, "INVALID_CREDENTIALS", "아이디/비밀번호를 확인해주세요."),
}