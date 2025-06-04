package noul.oe.core.user.adapter.out.info

import noul.oe.core.user.adapter.out.persistence.UserJpaRepository
import noul.oe.core.user.application.exception.UserNotFoundException
import noul.oe.support.port.UserInfoProvider
import org.springframework.stereotype.Component

@Component
class UserInfoProviderImpl(
    private val userRepository: UserJpaRepository
) : UserInfoProvider {

    override fun getUsername(userId: String): String {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("userId = $userId") }
            .username
    }
}