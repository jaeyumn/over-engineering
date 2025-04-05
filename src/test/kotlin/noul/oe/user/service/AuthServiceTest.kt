package noul.oe.user.service

import noul.oe.user.dto.request.UserLogInRequest
import noul.oe.user.entity.User
import noul.oe.user.exception.InvalidCredentialsException
import noul.oe.user.exception.UserErrorCode
import noul.oe.user.exception.UserNotFoundException
import noul.oe.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import java.time.LocalDateTime

class AuthServiceTest {
    private lateinit var sut: AuthService
    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        authenticationManager = mock<AuthenticationManager>()
        userRepository = mock<UserRepository>()
        sut = AuthService(userRepository, authenticationManager)
    }


    @Nested
    inner class LogInTests {
        @Test
        @DisplayName("username이 존재하지 않으면 UserNotFoundException이 발생한다")
        fun test1() {
            // given
            val request = createLogInRequest()
            whenever(userRepository.findByUsername(request.username)).thenReturn(null)

            // when & then
            assertThatThrownBy { sut.logIn(request) }
                .isInstanceOf(UserNotFoundException::class.java)
                .hasMessageContaining(UserErrorCode.USER_NOT_FOUND.message)
        }

        @Test
        @DisplayName("아이디 or 비밀번호 불일치 시, InvalidCredentialsException이 발생한다")
        fun test2() {
            // given
            val request = createLogInRequest()
//            val wrongPassword = "wrong123"
            val mockUser = mock<User> {
                on { password } doReturn "password"
            }
            whenever(userRepository.findByUsername(request.username)).thenReturn(mockUser)
            whenever(authenticationManager.authenticate(any()))
                .thenThrow(BadCredentialsException("Bad credentials"))

            // when & then
            assertThatThrownBy { sut.logIn(request) }
                .isInstanceOf(InvalidCredentialsException::class.java)
                .hasMessageContaining(UserErrorCode.INVALID_CREDENTIALS.message)
        }

        @Test
        @DisplayName("유효한 요청 시, 로그인에 성공한다")
        fun test100() {
            // given
            val request = createLogInRequest()
            val now = LocalDateTime.now()
            val mockUser = mock<User> {
                on { id } doReturn "UUID"
                on { username } doReturn request.username
                on { email } doReturn "test@example.com"
                on { createdAt } doReturn now
                on { modifiedAt } doReturn now
                on { password } doReturn "encodedPassword"
            }
            whenever(userRepository.findByUsername(request.username)).thenReturn(mockUser)
            whenever(authenticationManager.authenticate(any())).thenReturn(mock())

            // when
            val result = sut.logIn(request)

            // then
            assertThat(result).isNotNull
            assertThat(result.username).isEqualTo(request.username)
            verify(userRepository).findByUsername(request.username)
            verify(authenticationManager).authenticate(any())
        }

        private fun createLogInRequest() = UserLogInRequest(
            username = "testUser",
            password = "password123"
        )
    }
}