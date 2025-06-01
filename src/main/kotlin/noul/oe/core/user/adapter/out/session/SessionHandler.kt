package noul.oe.core.user.adapter.out.session

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SessionHandler(
    private val authenticationManager: AuthenticationManager,
) {
    fun setUpSecurityContext(username: String, password: String, request: HttpServletRequest) {
        val token = UsernamePasswordAuthenticationToken(username, password)
        val authentication = authenticationManager.authenticate(token)

        // SecurityContext 생성 및 설정
        val securiyContext = SecurityContextHolder.createEmptyContext()
        securiyContext.authentication = authentication
        SecurityContextHolder.setContext(securiyContext)

        // 세션에 SecurityContext 저장
        request.session.setAttribute("SPRING_SECURITY_CONTEXT", securiyContext)
    }

    fun logout(request: HttpServletRequest) {
        request.session.invalidate()
    }
}