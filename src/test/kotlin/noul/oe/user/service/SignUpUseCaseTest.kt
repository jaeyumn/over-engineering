package noul.oe.user.service

import noul.oe.core.user1.dto.request.UserSignUpRequest
import noul.oe.core.user1.entity.User
import noul.oe.core.user1.exception.EmailAlreadyExistsException
import noul.oe.core.user1.exception.UserErrorCode
import noul.oe.core.user1.exception.UsernameAlreadyExistsException
import noul.oe.core.user1.repository.UserRepository
import noul.oe.core.user1.service.UserService
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

class SignUpUseCaseTest {
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
}