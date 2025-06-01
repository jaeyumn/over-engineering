package noul.oe.core.user.domain.exception

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

open class UserException(
    private val errorCode: UserErrorCode,
    private val detail: String? = null
) : RuntimeException(errorCode.message) {
    init {
        val logMsg = buildString {
            append("[${errorCode.code} ${errorCode.message}")
            if (!detail.isNullOrBlank()) {
                append(" : $detail")
            }
        }
        logger.info { logMsg }
    }
}

class EmailAlreadyExistsException(email: String) : UserException(UserErrorCode.EMAIL_ALREADY_EXISTS)

class UsernameAlreadyExistsException(username: String) : UserException(UserErrorCode.USERNAME_ALREADY_EXISTS)

class UserNotFoundException(username: String) : UserException(UserErrorCode.USER_NOT_FOUND)

class InvalidCredentialsException(username: String) : UserException(UserErrorCode.INVALID_CREDENTIALS)
