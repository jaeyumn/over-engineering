package noul.oe.domain.user.exception

import noul.oe.support.exception.BaseException

class EmailAlreadyExistsException(logMessage: String) :
    BaseException(UserErrorCode.EMAIL_ALREADY_EXISTS, logMessage)

class UsernameAlreadyExistsException(logMessage: String) :
    BaseException(UserErrorCode.USERNAME_ALREADY_EXISTS, logMessage)

class UserNotFoundException(logMessage: String) :
    BaseException(UserErrorCode.USER_NOT_FOUND, logMessage)

class InvalidCredentialsException() : BaseException(UserErrorCode.INVALID_CREDENTIALS)