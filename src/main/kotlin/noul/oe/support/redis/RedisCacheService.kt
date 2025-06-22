package noul.oe.support.redis

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisCacheService(
    private val redisTemplate: StringRedisTemplate,
) {
    // 데이터 조회 및 캐싱 (조회될 때마다 TTL 갱신)
    fun <T> getOrSetWithLock(
        key: String,
        lockKey: String,
        ttlMinutes: Long,
        lockTimeoutSeconds: Long = 5,
        recheckDelayMillis: Long = 100,
        valueFetcher: () -> T? // DB 조회 람다 함수
    ): T? {
        // 1. 캐시 조회
        val cachedValue = redisTemplate.opsForValue().get(key)

        if (cachedValue != null) {
            redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES) // TTL 갱신
            return cachedValue as T
        }

        // 2. 캐시 없으면 락 획득 시도
        val lockAcquired = redisTemplate
            .opsForValue()
            .setIfAbsent(lockKey, "locked", lockTimeoutSeconds, TimeUnit.SECONDS)

        if (lockAcquired == true) { // 락 획득 성공 (DB 조회 및 캐싱)
            try {
                val dbValue = valueFetcher()
                if (dbValue != null) {
                    redisTemplate.opsForValue().set(key, dbValue.toString(), ttlMinutes, TimeUnit.MINUTES)
                }
                return dbValue

            } finally {
                redisTemplate.delete(lockKey) // 락 해제
            }

        } else { // 락 획득 실패
            Thread.sleep(recheckDelayMillis)
            val recheckedCache = redisTemplate.opsForValue().get(key) // delay 후 캐시 획득 재시도
            if (recheckedCache != null) {
                redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES)
                return recheckedCache as T
            } else {
                // 락 및 캐시 데이터를 못 얻었을 경우, DB 호출
                return valueFetcher()
            }
        }
    }

    // 캐시 제거
    fun evict(key: String) {
        redisTemplate.delete(key)
    }
}