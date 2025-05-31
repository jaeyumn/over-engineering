package noul.oe.support.security

import noul.oe.support.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class SecurityErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    UNAUTHENTICATED("UNAUTHENTICATED", "로그인된 사용자가 아닙니다.", HttpStatus.UNAUTHORIZED)
}