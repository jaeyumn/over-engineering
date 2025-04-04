package noul.oe.user.exception

import noul.oe.common.exception.BaseException

class EmailAlreadyExistsException : BaseException(UserErrorCode.EMAIL_ALREADY_EXISTS)
class UsernameAlreadyExistsException : BaseException(UserErrorCode.USERNAME_ALREADY_EXISTS)
class UserNotFoundException : BaseException(UserErrorCode.USER_NOT_FOUND)
class PasswordMismatchException : BaseException(UserErrorCode.PASSWORD_MISMATCH)