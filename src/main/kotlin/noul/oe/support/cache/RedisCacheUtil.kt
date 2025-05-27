package noul.oe.support.cache

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class RedisCacheUtil(
    private val redisTemplate: StringRedisTemplate
) {
    fun getOrLoad(
        key: String,
        ttl: Duration? = null,
        loader: () -> String // 캐시 값이 없다면 세팅
    ): String {
        val cached = redisTemplate.opsForValue().get(key)
        if (cached != null) {
            return cached
        }

        val loaded = loader()
        val ttlToUse = ttl ?: getRandomTTL()
        redisTemplate.opsForValue().set(key, loaded, ttlToUse.toMillis(), TimeUnit.MILLISECONDS)
        return loaded
    }

    fun put(key: String, value: String, ttl: Duration? = null) {
        val ttlToUse = ttl ?: getRandomTTL()
        redisTemplate.opsForValue().set(key, value, ttlToUse.toMillis(), TimeUnit.MILLISECONDS)
    }

    fun remove(key: String) {
        redisTemplate.delete(key)
    }

    // TTL 없을 경우, 10 ~ 20분 사이로 랜덤하게 설정
    private fun getRandomTTL(): Duration {
        val randomMinutes = (10..20).random()
        return Duration.ofMinutes(randomMinutes.toLong())
    }
}