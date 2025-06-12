package noul.oe.core.user.application

import noul.oe.core.user.application.exception.InvalidCredentialsException
import noul.oe.core.user.application.exception.UserNotFoundException
import noul.oe.core.user.application.port.input.LoginCommand
import noul.oe.core.user.application.port.output.UserRepositoryPort
import noul.oe.core.user.application.usecase.LoginUseCase
import noul.oe.core.user.domain.User
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

class LoginUseCaseTest {

    private lateinit var userRepositoryPort: UserRepositoryPort
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var sut: LoginUseCase

    private val testUser = User(
        id = "testId",
        username = "testUser",
        email = "test@test.com",
        password = "encodedPassword",
        createdAt = LocalDateTime.now(),
        modifiedAt = LocalDateTime.now(),
    )

    @BeforeEach
    fun setUp() {
        userRepositoryPort = mock()
        passwordEncoder = mock()
        sut = LoginUseCase(userRepositoryPort, passwordEncoder)
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인 시 예외 발생")
    fun test1() {
        // givne
        val command = LoginCommand("noneUser", "password")

        whenever(userRepositoryPort.findByUsername(command.username)).thenReturn(null)

        // when & then
        assertThatThrownBy { sut.login(command) }
            .isInstanceOf(UserNotFoundException::class.java)
            .hasMessageContaining(command.username)
    }

    @Test
    @DisplayName("비밀번호 불일치 시 예외 발생")
    fun test2() {
        // given
        val command = LoginCommand("testUser", "wrongPassword")
        val encodedPassword = "encodedPassword"

        whenever(userRepositoryPort.findByUsername(command.username)).thenReturn(testUser)
        whenever(passwordEncoder.matches(command.password, encodedPassword)).thenReturn(false)

        // when & then
        assertThatThrownBy { sut.login(command) }
            .isInstanceOf(InvalidCredentialsException::class.java)
            .hasMessageContaining(command.username)
    }

    @Test
    @DisplayName("로그인 성공")
    fun test100() {
        // given
        val command = LoginCommand("testUser", "password")
        val encodedPassword = "encodedPassword"

        whenever(userRepositoryPort.findByUsername(command.username)).thenReturn(testUser)
        whenever(passwordEncoder.matches(command.password, encodedPassword)).thenReturn(true)

        // when & then
        assertThatCode { sut.login(command) }
            .doesNotThrowAnyException()
    }
}