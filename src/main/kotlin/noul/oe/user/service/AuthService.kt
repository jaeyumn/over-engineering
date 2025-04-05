package noul.oe.user.service

import jakarta.servlet.http.HttpServletRequest
import noul.oe.user.dto.request.UserLogInRequest
import noul.oe.user.dto.response.UserResponse
import noul.oe.user.exception.InvalidCredentialsException
import noul.oe.user.exception.UserNotFoundException
import noul.oe.user.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
) {
    fun logIn(request: UserLogInRequest): UserResponse {
        val authenticationToken = UsernamePasswordAuthenticationToken(request.username, request.password)
        val authentication = try {
            authenticationManager.authenticate(authenticationToken)
        } catch (e: BadCredentialsException) {
            throw InvalidCredentialsException()
        }
        SecurityContextHolder.getContext().authentication = authentication

        val user = userRepository.findByUsername(request.username) ?: throw UserNotFoundException()

        return UserResponse.from(user)
    }

    fun logOut(request: HttpServletRequest) {
        request.session.invalidate()
    }
}