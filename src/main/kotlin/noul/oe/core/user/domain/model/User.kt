package noul.oe.core.user.domain.model

import java.time.LocalDateTime

data class User(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val role: Role = Role.USER,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)
