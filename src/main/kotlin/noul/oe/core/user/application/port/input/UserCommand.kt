package noul.oe.core.user.application.port.input

import noul.oe.core.user.domain.model.Role
import noul.oe.core.user.domain.model.User
import java.time.LocalDateTime
import java.util.*

data class LoginCommand(
    val username: String,
    val password: String,
)

data class SignUpCommand(
    val username: String,
    val email: String,
    val password: String,
) {
    fun toDomain(encodedPassword: String): User =
        User(
            id = UUID.randomUUID().toString(),
            username = username,
            email = email,
            password = encodedPassword,
            role = Role.USER,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
}