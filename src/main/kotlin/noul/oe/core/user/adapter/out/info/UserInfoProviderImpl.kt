package noul.oe.core.user.adapter.out.info

import noul.oe.core.user.adapter.out.persistence.UserJpaRepository
import noul.oe.core.user.application.exception.UserNotFoundException
import noul.oe.support.port.UserInfoProvider
import noul.oe.support.redis.CacheKey
import noul.oe.support.redis.RedisCacheService
import org.springframework.stereotype.Component

@Component
class UserInfoProviderImpl(
    private val userRepository: UserJpaRepository,
    private val redisCacheService: RedisCacheService,
) : UserInfoProvider {

    override fun getUsername(userId: String): String {
        val cacheKey = CacheKey.USERNAME.with(userId)
        val lockKey = CacheKey.USERNAME.lockFor(userId)
        val ttlMinutes = 24 * 60L

        val username = redisCacheService.getOrSetWithLock(
            key = cacheKey,
            lockKey = lockKey,
            ttlMinutes = ttlMinutes,
            valueFetcher = {
                userRepository.findById(userId)
                    .orElseThrow { UserNotFoundException("userId = $userId") }
                    .username
            }
        )

        return username ?: "Unkown User"
    }
}