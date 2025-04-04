package noul.oe.user.dto.response

import noul.oe.user.entity.User
import java.time.LocalDateTime

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                createdAt = user.createdAt,
                modifiedAt = user.modifiedAt,
            )
        }
    }
}