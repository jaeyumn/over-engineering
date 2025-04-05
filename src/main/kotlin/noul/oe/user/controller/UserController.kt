package noul.oe.user.controller

import jakarta.validation.Valid
import noul.oe.common.response.ApiResponse
import noul.oe.user.dto.request.UserLogInRequest
import noul.oe.user.dto.request.UserSignUpRequest
import noul.oe.user.dto.response.UserResponse
import noul.oe.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/sign-up")
    fun signUp(@Valid @RequestBody request: UserSignUpRequest): ResponseEntity<ApiResponse<Nothing>> {
        userService.signUp(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success())
    }
}