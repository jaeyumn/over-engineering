package noul.oe.core.user.application

import noul.oe.core.user.application.exception.EmailAlreadyExistsException
import noul.oe.core.user.application.exception.UsernameAlreadyExistsException
import noul.oe.core.user.application.port.input.SignUpCommand
import noul.oe.core.user.application.port.output.UserRepositoryPort
import noul.oe.core.user.application.usecase.SignUpUseCase
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

class SignUpUseCaseTest {
    private lateinit var userRepositoryPort: UserRepositoryPort
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var sut: SignUpUseCase

    @BeforeEach
    fun setUp() {
        userRepositoryPort = mock()
        passwordEncoder = mock()
        sut = SignUpUseCase(userRepositoryPort, passwordEncoder)
    }

    @Test
    @DisplayName("이메일이 중복되면 예외가 발생한다")
    fun test1() {
        // given
        val command = SignUpCommand("testUser", "existsEmail", "password")
        whenever(userRepositoryPort.existsByEmail(command.email)).thenReturn(true)

        // when & then
        assertThatThrownBy { sut.signUp(command) }
            .isInstanceOf(EmailAlreadyExistsException(command.email)::class.java)
            .hasMessageContaining(command.email)
    }

    @Test
    @DisplayName("유저네임이 중복되면 예외가 발생한다")
    fun test2() {
        // given
        val command = SignUpCommand("existsUsername", "test@test.com", "password")
        whenever(userRepositoryPort.existsByUsername(command.username)).thenReturn(true)

        // when & then
        assertThatThrownBy { sut.signUp(command) }
            .isInstanceOf(UsernameAlreadyExistsException(command.username)::class.java)
            .hasMessageContaining(command.username)
    }

    @Test
    @DisplayName("회원가입에 성공한다")
    fun test100() {
        // given
        val command = SignUpCommand("testUser", "test@test.com", "password")
        val encodedPassword = "encodedPassword"

        whenever(userRepositoryPort.existsByEmail(command.email)).thenReturn(false)
        whenever(userRepositoryPort.existsByUsername(command.email)).thenReturn(false)
        whenever(passwordEncoder.encode(command.password)).thenReturn(encodedPassword)

        // when & then
        assertThatCode { sut.signUp(command) }
            .doesNotThrowAnyException()

        verify(userRepositoryPort).save(
            check {
                assert(it.username == command.username)
                assert(it.email == command.email)
                assert(it.password == encodedPassword)
            }
        )
    }
}