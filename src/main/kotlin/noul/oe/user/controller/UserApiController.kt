package noul.oe.user.controller

import jakarta.validation.Valid
import noul.oe.user.dto.request.UserSignUpRequest
import noul.oe.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserApiController(
    private val userService: UserService
) {
    /**
     * 회원가입
     */
    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody request: UserSignUpRequest): ResponseEntity<Void> {
        userService.signUp(request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}