package noul.oe.user.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import noul.oe.user.entity.User

data class UserSignUpRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val username: String,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    @field:Size(min = 3, max = 255)
    val password: String,
) {
    fun toEntity(password: String): User {
        return User(
            username = this.username,
            email = this.email,
            password = password
        )
    }
}