package noul.oe.core.user.adapter.`in`.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import noul.oe.core.user.adapter.`in`.web.dto.UserLoginRequest
import noul.oe.core.user.adapter.`in`.web.dto.UserLoginResponse
import noul.oe.core.user.adapter.out.session.SessionHandler
import noul.oe.core.user.application.port.input.AuthCommandPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthApiController(
    private val authCommandPort: AuthCommandPort,
    private val sessionHandler: SessionHandler,
) {

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody requestDto: UserLoginRequest,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<UserLoginResponse> {
        val command = requestDto.toCommand()
        val result = authCommandPort.login(command)

        // 세션 set
        sessionHandler.setUpSecurityContext(command.username, command.password, httpRequest)

        return ResponseEntity.ok(UserLoginResponse.from(result))
    }

    @PostMapping("/logout")
    fun logout(httpRequest: HttpServletRequest): ResponseEntity<Void> {
        sessionHandler.logout(httpRequest)

        return ResponseEntity.noContent().build()
    }
}