package noul.oe.core.user.adapter.`in`.web.api

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import noul.oe.core.user.application.port.input.LoginCommand

data class UserLoginRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val username: String,

    @field:NotBlank
    @field:Size(min = 3, max = 255)
    val password: String,
) {
    fun toCommand(): LoginCommand = LoginCommand(username, password)
}