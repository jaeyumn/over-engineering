package noul.oe.user.service

import jakarta.servlet.http.HttpServletRequest
import noul.oe.user.dto.request.UserLogInRequest
import noul.oe.user.dto.response.UserResponse
import noul.oe.user.exception.UserNotFoundException
import noul.oe.user.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
) {
    fun login(request: UserLogInRequest, httpRequest: HttpServletRequest): UserResponse {
        val user = userRepository.findByUsername(request.username) ?: throw UserNotFoundException()

        val authenticationToken = UsernamePasswordAuthenticationToken(request.username, request.password)
        val authentication = authenticationManager.authenticate(authenticationToken)

        // SecurityContext 생성 및 설정
        val securityContext: SecurityContext = SecurityContextHolder.createEmptyContext()
        securityContext.authentication = authentication
        SecurityContextHolder.setContext(securityContext)

        // 세션에 SecurityContext 저장
        httpRequest.session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext)

        return UserResponse.from(user)
    }

    fun logout(request: HttpServletRequest) {
        request.session.invalidate()
    }
}