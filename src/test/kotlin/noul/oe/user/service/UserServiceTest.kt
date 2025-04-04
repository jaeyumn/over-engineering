package noul.oe.user.service

import noul.oe.user.dto.request.UserLogInRequest
import noul.oe.user.dto.request.UserSignUpRequest
import noul.oe.user.entity.User
import noul.oe.user.exception.*
import noul.oe.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

class UserServiceTest {
    private lateinit var sut: UserService
    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        passwordEncoder = BCryptPasswordEncoder()
        sut = UserService(userRepository, passwordEncoder)
    }

    @Nested
    inner class SignUpTests {
        @Test
        @DisplayName("이메일 중복 시, EmailAlreadyExistsException이 발생한다")
        fun test1() {
            // given
            val request = createUserSignUpRequest()
            whenever(userRepository.existsByEmail(request.email)).thenReturn(true)

            // when & then
            assertThatThrownBy { sut.signUp(request) }
                .isInstanceOf(EmailAlreadyExistsException::class.java)
                .hasMessageContaining(UserErrorCode.EMAIL_ALREADY_EXISTS.message)
        }

        @Test
        @DisplayName("유저이름 중복 시, UsernameAlreadyExistsException이 발생한다")
        fun test2() {
            // given
            val request = createUserSignUpRequest()
            whenever(userRepository.existsByUsername(request.username)).thenReturn(true)

            // when & then
            assertThatThrownBy { sut.signUp(request) }
                .isInstanceOf(UsernameAlreadyExistsException::class.java)
                .hasMessageContaining(UserErrorCode.USERNAME_ALREADY_EXISTS.message)
        }

        @Test
        @DisplayName("유효한 요청 시, 회원가입에 성공한다")
        fun test100() {
            // given
            val request = createUserSignUpRequest()
            whenever(userRepository.existsByEmail(anyString())).thenReturn(false)
            whenever(userRepository.existsByUsername(anyString())).thenReturn(false)
            whenever(userRepository.save(any())).thenReturn(mock(User::class.java))

            // when
            sut.signUp(request)

            // then
            verify(userRepository, times(1)).save(any())
        }

        private fun createUserSignUpRequest() = UserSignUpRequest(
            username = "testUser",
            email = "test@test.com",
            password = "password123"
        )
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
        @DisplayName("비밀번호 불일치 시, PasswordMismatchException이 발생한다")
        fun test2() {
            // given
            val request = createLogInRequest()
            val wrongPassword = "wrong123"
            val mockUser = mock<User> {
                on { password } doReturn passwordEncoder.encode(wrongPassword)
            }
            whenever(userRepository.findByUsername(request.username)).thenReturn(mockUser)

            // when & then
            assertThatThrownBy { sut.logIn(request) }
                .isInstanceOf(PasswordMismatchException::class.java)
                .hasMessageContaining(UserErrorCode.PASSWORD_MISMATCH.message)
        }

        @Test
        @DisplayName("유효한 요청 시, 로그인에 성공한다")
        fun test100() {
            // given
            val request = createLogInRequest()
            val mockUser = mock<User> {
                on { id } doReturn "UUID"
                on { username } doReturn "testUser"
                on { email } doReturn "test@example.com"
                on { createdAt } doReturn LocalDateTime.now()
                on { modifiedAt } doReturn LocalDateTime.now()
                on { password } doReturn passwordEncoder.encode(request.password)
            }
            whenever(userRepository.findByUsername(request.username)).thenReturn(mockUser)

            // when
            val result = sut.logIn(request)

            // then
            assertThat(result).isNotNull
            verify(userRepository).findByUsername(request.username)
        }

        private fun createLogInRequest() = UserLogInRequest(
            username = "testUser",
            password = "password123"
        )
    }
}