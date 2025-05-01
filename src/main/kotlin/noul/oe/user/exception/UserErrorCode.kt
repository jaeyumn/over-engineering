package noul.oe.user.exception

import noul.oe.support.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "이미 존재하는 이름입니다.", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("USER_NOT_FOUND", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "아이디/비밀번호를 확인해주세요.", HttpStatus.UNAUTHORIZED),
}