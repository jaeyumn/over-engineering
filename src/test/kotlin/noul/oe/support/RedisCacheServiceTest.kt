package noul.oe.support

import noul.oe.support.redis.RedisCacheService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.TimeUnit

class RedisCacheServiceTest {

    private lateinit var sut: RedisCacheService

    private val redisTemplate: StringRedisTemplate = mock()
    private val valueOperations: ValueOperations<String, String> = mock()

    private val key = "testKey"
    private val lockKey = "${key}Lock"
    private val lockedValue = "locked"
    private val cachedValue = "cachedValue"
    private val ttlMinutes = 10L

    @BeforeEach
    fun setUp() {
        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        sut = RedisCacheService(redisTemplate)
    }

    @Test
    @DisplayName("캐시 히트 시, 캐시된 값을 반환하고 TTL을 갱신한다")
    fun test1() {
        // given
        whenever(valueOperations.get(key)).thenReturn(cachedValue)

        // when
        val result = sut.getOrSetWithLock(
            key = key,
            lockKey = lockKey,
            ttlMinutes = ttlMinutes,
            valueFetcher = { "newValue" }
        )

        // then
        assertThat(cachedValue).isEqualTo(result)
        verify(valueOperations, never()).setIfAbsent(any(), any(), any(), any())
        verify(redisTemplate).expire(key, ttlMinutes, TimeUnit.MINUTES)
    }

    @DisplayName("캐시 미스 시, 락을 획득하고 DB에서 값을 가져와 캐싱한다")
    @Test
    fun test2() {
        // given
        val dbValue = "valueFromDB"
        whenever(valueOperations.get(key)).thenReturn(null)
        whenever(valueOperations.setIfAbsent(eq(lockKey), eq(lockedValue), any(), any()))
            .thenReturn(true)

        // when
        val result = sut.getOrSetWithLock(
            key = key,
            lockKey = lockKey,
            ttlMinutes = ttlMinutes,
            valueFetcher = { dbValue }
        )

        // then
        assertThat(result).isEqualTo(dbValue)
        verify(valueOperations).setIfAbsent(eq(lockKey), eq(lockedValue), any(), any())
        verify(valueOperations).set(key, dbValue, ttlMinutes, TimeUnit.MINUTES)
        verify(redisTemplate).delete(lockKey)
        verify(redisTemplate, never()).expire(any(), any(), any())
    }

    @DisplayName("다른 스레드가 락을 획득했을 때, 짧게 대기 후 캐시를 재확인하고 값을 반환한다")
    @Test
    fun test3() {
        // given
        val existingCachedValue = "anotherThreadCachedValue"
        whenever(valueOperations.get(key)).thenReturn(null, existingCachedValue)
        whenever(valueOperations.setIfAbsent(eq(lockKey), eq(lockedValue), any(), any()))
            .thenReturn(false)

        // when
        val result = sut.getOrSetWithLock(
            key = key,
            lockKey = lockKey,
            ttlMinutes = ttlMinutes,
            recheckDelayMillis = 10,
            valueFetcher = { "dbValueShouldNotBeFetched" }
        )

        // then
        assertThat(result).isEqualTo(existingCachedValue)
        verify(valueOperations, times(2)).get(key)
        verify(valueOperations).setIfAbsent(eq(lockKey), eq(lockedValue), any(), any())
        verify(redisTemplate).expire(key, ttlMinutes, TimeUnit.MINUTES)
    }

    @Test
    @DisplayName("다른 스레드가 락을 획득했지만 캐싱이 완료되지 않았을 때, DB에서 값을 가져온다")
    fun test4() {
        // Given
        val fallbackDbValue = "fallbackFromDB"

        whenever(valueOperations.get(key)).thenReturn(null, null)
        whenever(valueOperations.setIfAbsent(eq(lockKey), eq("locked"), any(), any()))
            .thenReturn(false)

        // When
        val result = sut.getOrSetWithLock(
            key = key,
            lockKey = lockKey,
            ttlMinutes = ttlMinutes,
            recheckDelayMillis = 10,
            valueFetcher = { fallbackDbValue }
        )

        // Then
        assertThat(result).isEqualTo(fallbackDbValue)
        verify(valueOperations, times(2)).get(key)
        verify(valueOperations).setIfAbsent(eq(lockKey), eq("locked"), any(), any())
        verify(valueOperations, never()).set(any(), any(), any(), any())
        verify(redisTemplate, never()).expire(any(), any(), any())
    }

    @Test
    @DisplayName("evict 호출 시 Redis에서 해당 키를 삭제한다")
    fun test5() {
        // Given
        val keyToEvict = "keyToDelete"

        // When
        sut.evict(keyToEvict)

        // Then
        verify(redisTemplate).delete(keyToEvict)
    }
}