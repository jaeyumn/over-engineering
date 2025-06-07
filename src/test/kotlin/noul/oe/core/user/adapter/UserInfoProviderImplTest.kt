package noul.oe.core.user.adapter

import noul.oe.core.user.adapter.out.info.UserInfoProviderImpl
import noul.oe.core.user.adapter.out.persistence.UserJpaEntity
import noul.oe.core.user.adapter.out.persistence.UserJpaRepository
import noul.oe.core.user.application.exception.UserNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserInfoProviderImplTest {

    @Mock
    lateinit var userJpaRepository: UserJpaRepository

    lateinit var sut: UserInfoProviderImpl

    @BeforeEach
    fun setUp() {
        userJpaRepository = mock()
        sut = UserInfoProviderImpl(userJpaRepository)
    }

    @Test
    @DisplayName("존재하지 않는 회원일 경우 예외가 발생한다")
    fun test1() {
        // given
        val userId = "testId"

        whenever(userJpaRepository.findById(userId)).thenReturn(Optional.empty())

        // when & then
        assertThatThrownBy { sut.getUsername(userId) }
            .isInstanceOf(UserNotFoundException::class.java)
            .hasMessageContaining(userId)
    }

    @Test
    @DisplayName("정상 조회 시 username을 반환한다")
    fun test100() {
        // given
        val userId = "testId"
        val mockUser = UserJpaEntity(userId, "testUser", "test@test.com", "encodedPassword")

        whenever(userJpaRepository.findById(userId)).thenReturn(Optional.of(mockUser))

        // when
        val result = sut.getUsername(userId)

        // then
        assertThat(result).isEqualTo(mockUser.username)
    }
}