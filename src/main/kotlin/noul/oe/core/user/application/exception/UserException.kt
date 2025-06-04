package noul.oe.core.user.application.exception

import noul.oe.support.exception.DomainException

class EmailAlreadyExistsException(email: String) : DomainException(UserErrorCode.EMAIL_ALREADY_EXISTS, email)

class UsernameAlreadyExistsException(username: String) :
    DomainException(UserErrorCode.USERNAME_ALREADY_EXISTS, username)

class UserNotFoundException(username: String) : DomainException(UserErrorCode.USER_NOT_FOUND, username)

class InvalidCredentialsException(username: String) : DomainException(UserErrorCode.INVALID_CREDENTIALS, username)
