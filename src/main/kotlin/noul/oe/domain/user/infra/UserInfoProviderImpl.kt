package noul.oe.domain.user.infra

import noul.oe.domain.user.exception.UserNotFoundException
import noul.oe.domain.user.repository.UserRepository
import noul.oe.support.info.UserInfoProvider
import org.springframework.stereotype.Component

@Component
class UserInfoProviderImpl(
    private val userRepository: UserRepository
) : UserInfoProvider {

    override fun getUsername(userId: String): String {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found: userId=$userId") }
            .username
    }
}