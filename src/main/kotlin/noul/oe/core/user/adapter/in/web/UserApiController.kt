package noul.oe.core.user.adapter.`in`.web

import jakarta.validation.Valid
import noul.oe.core.user.adapter.`in`.web.dto.UserSignUpRequest
import noul.oe.core.user.application.port.input.UserCommandPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserApiController(
    private val userCommandPort: UserCommandPort,
) {
    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody request: UserSignUpRequest): ResponseEntity<Void> {
        val command = request.toCommand()
        userCommandPort.signUp(command)

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}