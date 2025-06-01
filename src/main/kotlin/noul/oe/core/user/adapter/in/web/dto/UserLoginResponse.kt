package noul.oe.core.user.adapter.`in`.web.dto

import noul.oe.core.user.application.port.input.LoginResult
import java.time.LocalDateTime

data class UserLoginResponse(
    val id: String,
    val username: String,
    val email: String,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    companion object {
        fun from(result: LoginResult): UserLoginResponse =
            UserLoginResponse(
                id = result.userId,
                username = result.username,
                email = result.email,
                createdAt = result.createdAt,
                modifiedAt = result.modifiedAt,
            )
    }
}