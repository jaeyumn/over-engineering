package noul.oe.core.user.adapter

import noul.oe.core.user.adapter.out.persistence.UserJpaEntity
import noul.oe.core.user.adapter.out.persistence.UserJpaRepository
import noul.oe.core.user.adapter.out.security.UserDetailsLoaderImpl
import noul.oe.core.user.application.exception.UserNotFoundException
import noul.oe.support.security.UserPrincipal
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UesrDetailsLoaderImplTest {

    @Mock
    lateinit var userJpaRepository: UserJpaRepository

    lateinit var sut: UserDetailsLoaderImpl

    @BeforeEach
    fun setUp() {
        userJpaRepository = mock()
        sut = UserDetailsLoaderImpl(userJpaRepository)
    }

    @Test
    @DisplayName("사용자가 존재하지 않으면 예외가 발생한다")
    fun test1() {
        // given
        val username = "testUser"
        whenever(userJpaRepository.findByUsername(username)).thenReturn(null)

        // when & then
        Assertions.assertThatThrownBy { sut.loadUserByUsername(username) }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    @DisplayName("사용자가 존재하면 UserPrincipal을 반환한다")
    fun test100() {
        // given
        val now = LocalDateTime.now()
        val user = UserJpaEntity(
            id = "testId",
            username = "testUser",
            email = "test@test.com",
            password = "encodedPassword",
            createdAt = now,
            modifiedAt = now,
        )

        whenever(userJpaRepository.findByUsername("testUser")).thenReturn(user)

        // when
        val result = sut.loadUserByUsername("testUser")

        // then
        assertThat(result).isInstanceOf(UserPrincipal::class.java)
    }
}