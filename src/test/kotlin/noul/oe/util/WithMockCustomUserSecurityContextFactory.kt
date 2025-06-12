package noul.oe.util

import noul.oe.support.security.UserPrincipal
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockCustomUserSecurityContextFactory : WithSecurityContextFactory<WithMockCustomUser> {

    override fun createSecurityContext(annotation: WithMockCustomUser): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()

        val authorities = annotation.roles.map { SimpleGrantedAuthority(it) }
        val principal = UserPrincipal(
            userId = annotation.userId,
            username = annotation.username,
            password = annotation.password,
            authorities = authorities
        )
        val authentication = UsernamePasswordAuthenticationToken(principal, null, authorities)
        context.authentication = authentication

        return context
    }
}