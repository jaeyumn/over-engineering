package noul.oe.core.user.adapter.out.persistence

import noul.oe.core.user.application.port.output.UserRepositoryPort
import noul.oe.core.user.domain.User
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryAdapter(
    private val userRepository: UserJpaRepository,
) : UserRepositoryPort {

    override fun save(user: User) {
        val entity = UserMapper.toEntity(user)
        userRepository.save(entity)
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)?.let {
            UserMapper.toDomain(it)
        }
    }

    override fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    override fun existsByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }
}