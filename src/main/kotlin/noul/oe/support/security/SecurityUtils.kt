package noul.oe.support.security

import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtils {
    fun getCurrentUser(): UserPrincipal {
        val auth = SecurityContextHolder.getContext().authentication
        return auth.principal as? UserPrincipal ?: throw UnauthenticatedException()
    }
}