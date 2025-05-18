package noul.oe.domain.user.repository

import noul.oe.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String> {
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): User?
}