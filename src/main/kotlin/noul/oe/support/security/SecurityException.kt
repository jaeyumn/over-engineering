package noul.oe.support.security

import noul.oe.support.exception.DomainException

class UnauthenticatedException :
    DomainException(SecurityErrorCode.UNAUTHENTICATED, "Unauthenticated access")