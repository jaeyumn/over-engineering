package noul.oe.core.user.application.usecase

import noul.oe.core.user.application.exception.InvalidCredentialsException
import noul.oe.core.user.application.exception.UserNotFoundException
import noul.oe.core.user.application.port.input.AuthCommandPort
import noul.oe.core.user.application.port.input.LoginCommand
import noul.oe.core.user.application.port.output.UserRepositoryPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LoginUseCase(
    private val userRepositoryPort: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
) : AuthCommandPort {

    override fun login(command: LoginCommand) {
        val username = command.username
        val user = userRepositoryPort.findByUsername(username)
            ?: throw UserNotFoundException(username)

        // 비밀번호 검증
        if (!passwordEncoder.matches(command.password, user.password)) {
            throw InvalidCredentialsException(command.username)
        }
    }
}