package noul.oe.core.user.application.port.output

import noul.oe.core.user.domain.User

interface UserRepositoryPort {
    fun save(user: User)
    fun findByUsername(username: String): User?
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
}