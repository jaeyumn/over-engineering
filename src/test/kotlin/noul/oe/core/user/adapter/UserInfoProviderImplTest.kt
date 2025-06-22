package noul.oe.core.user.adapter

import noul.oe.core.user.adapter.out.info.UserInfoProviderImpl
import noul.oe.core.user.adapter.out.persistence.UserJpaEntity
import noul.oe.core.user.adapter.out.persistence.UserJpaRepository
import noul.oe.core.user.application.exception.UserNotFoundException
import noul.oe.support.redis.CacheKey
import noul.oe.support.redis.RedisCacheService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserInfoProviderImplTest {

    @Mock
    lateinit var userJpaRepository: UserJpaRepository

    @Mock
    lateinit var redisCacheService: RedisCacheService

    lateinit var sut: UserInfoProviderImpl

    private val userId = "testId"
    private val cacheKey = CacheKey.USERNAME.with(userId)
    private val lockKey = CacheKey.USERNAME.lockFor(userId)

    @BeforeEach
    fun setUp() {
        userJpaRepository = mock()
        sut = UserInfoProviderImpl(userJpaRepository, redisCacheService)
    }

    @Test
    @DisplayName("존재하지 않는 회원일 경우 예외가 발생한다")
    fun test1() {
        // given
        whenever(redisCacheService.getOrSetWithLock(
            key = eq(cacheKey),
            lockKey = eq(lockKey),
            ttlMinutes = any(),
            lockTimeoutSeconds = any(),
            recheckDelayMillis = any(),
            valueFetcher = any<() -> String>()
        )).thenAnswer { invocation ->
            val valueFetcher: () -> String? = invocation.getArgument(5)
            valueFetcher.invoke()
        }
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
        val mockUser = UserJpaEntity(userId, "testUser", "test@test.com", "encodedPassword")

        whenever(redisCacheService.getOrSetWithLock(
            key = eq(cacheKey),
            lockKey = eq(lockKey),
            ttlMinutes = any(),
            lockTimeoutSeconds = any(),
            recheckDelayMillis = any(),
            valueFetcher = any<() -> String?>()
        )).thenAnswer { invocation ->
            val valueFetcher: () -> String? = invocation.getArgument(5)
            valueFetcher.invoke()
        }
        whenever(userJpaRepository.findById(userId)).thenReturn(Optional.of(mockUser))

        // when
        val result = sut.getUsername(userId)

        // then
        assertThat(result).isEqualTo(mockUser.username)
    }
}