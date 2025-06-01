package noul.oe.core.user.domain.exception


enum class UserErrorCode(
    val status: Int,
    val code: String,
    val message: String,
) {
    USERNAME_ALREADY_EXISTS(409, "USERNAME_ALREADY_EXISTS", "이미 존재하는 이름입니다."),
    EMAIL_ALREADY_EXISTS(409, "EMAIL_ALREADY_EXISTS", "이미 존재하는 이메일입니다."),
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", "아이디/비밀번호를 확인해주세요."),
}