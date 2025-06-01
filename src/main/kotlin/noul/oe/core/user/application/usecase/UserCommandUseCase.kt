package noul.oe.core.user.application.usecase

import noul.oe.core.user.application.port.input.*
import noul.oe.core.user.application.port.output.UserRepositoryPort
import noul.oe.core.user.domain.exception.EmailAlreadyExistsException
import noul.oe.core.user.domain.exception.InvalidCredentialsException
import noul.oe.core.user.domain.exception.UserNotFoundException
import noul.oe.core.user.domain.exception.UsernameAlreadyExistsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandUseCase(
    private val userRepositoryPort: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
) : UserCommandPort, AuthCommandPort {

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

    override fun login(command: LoginCommand): LoginResult {
        val username = command.username
        val user = userRepositoryPort.findByUsername(username)
            ?: throw UserNotFoundException(username)

        // 비밀번호 검증
        if (!passwordEncoder.matches(command.password, user.password)) {
            throw InvalidCredentialsException(command.username)
        }

        return LoginResult(
            userId = user.id,
            username = user.username,
            email = user.email,
            createdAt = user.createdAt,
            modifiedAt = user.modifiedAt,
        )
    }
}