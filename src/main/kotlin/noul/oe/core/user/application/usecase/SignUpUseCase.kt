package noul.oe.core.user.application.usecase

import noul.oe.core.user.application.exception.EmailAlreadyExistsException
import noul.oe.core.user.application.exception.UsernameAlreadyExistsException
import noul.oe.core.user.application.port.input.SignUpCommand
import noul.oe.core.user.application.port.input.UserCommandPort
import noul.oe.core.user.application.port.output.UserRepositoryPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SignUpUseCase(
    private val userRepositoryPort: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
) : UserCommandPort {

    override fun signUp(command: SignUpCommand) {
        // 이메일 중복 체크
        val email = command.email
        if (userRepositoryPort.existsByEmail(email)) {
            throw EmailAlreadyExistsException(email)
        }

        // 유저네임 중복 체크
        val username = command.username
        if (userRepositoryPort.existsByUsername(username)) {
            throw UsernameAlreadyExistsException(username)
        }

        val encodedPassword = passwordEncoder.encode(command.password)
        val user = command.toDomain(encodedPassword)
        userRepositoryPort.save(user)
    }
}