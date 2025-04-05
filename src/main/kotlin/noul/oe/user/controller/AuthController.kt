package noul.oe.user.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import noul.oe.common.response.ApiResponse
import noul.oe.user.dto.request.UserLogInRequest
import noul.oe.user.dto.response.UserResponse
import noul.oe.user.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/log-in")
    fun logIn(@Valid @RequestBody request: UserLogInRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val response = authService.logIn(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/log-out")
    fun logOut(request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        authService.logOut(request)
        return ResponseEntity.ok(ApiResponse.success())
    }
}