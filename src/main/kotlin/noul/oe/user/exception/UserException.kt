package noul.oe.user.exception

import noul.oe.support.exception.BaseException

class EmailAlreadyExistsException : BaseException(UserErrorCode.EMAIL_ALREADY_EXISTS)
class UsernameAlreadyExistsException : BaseException(UserErrorCode.USERNAME_ALREADY_EXISTS)
class UserNotFoundException : BaseException(UserErrorCode.USER_NOT_FOUND)
class InvalidCredentialsException : BaseException(UserErrorCode.INVALID_CREDENTIALS)