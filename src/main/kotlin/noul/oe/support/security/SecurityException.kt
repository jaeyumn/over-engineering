package noul.oe.support.security

import noul.oe.support.exception.BaseException

class UnauthenticatedException(logMessage: String = "Unauthenticated access") :
    BaseException(SecurityErrorCode.UNAUTHENTICATED, logMessage)