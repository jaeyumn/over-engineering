package noul.oe.core.user.application.port.input

import java.time.LocalDateTime

data class LoginResult(
    val userId: String,
    val username: String,
    val email: String,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)