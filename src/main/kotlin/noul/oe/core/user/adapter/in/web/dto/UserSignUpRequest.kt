package noul.oe.core.user.adapter.`in`.web.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import noul.oe.core.user.application.port.input.SignUpCommand

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
    fun toCommand(): SignUpCommand =
        SignUpCommand(username, email, password)
}
