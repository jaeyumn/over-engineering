package noul.oe.user.service

import noul.oe.user.dto.request.UserLogInRequest
import noul.oe.user.dto.request.UserSignUpRequest
import noul.oe.user.dto.response.UserResponse
import noul.oe.user.exception.EmailAlreadyExistsException
import noul.oe.user.exception.PasswordMismatchException
import noul.oe.user.exception.UserNotFoundException
import noul.oe.user.exception.UsernameAlreadyExistsException
import noul.oe.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun signUp(request: UserSignUpRequest) {
        validateSignUpRequest(request);

        val encodedPassword = passwordEncoder.encode(request.password)
        val user = request.toEntity(encodedPassword);
        userRepository.save(user)
    }

    @Transactional
    fun logIn(request: UserLogInRequest): UserResponse {
        val user = userRepository.findByUsername(request.username)
            ?: throw UserNotFoundException()

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw PasswordMismatchException()
        }

        return UserResponse.from(user)
    }

    private fun validateSignUpRequest(request: UserSignUpRequest) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException()
        }
        // 유저네임 중복 체크
        if (userRepository.existsByUsername(request.username)) {
            throw UsernameAlreadyExistsException()
        }
    }
}