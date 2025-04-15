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
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: UserLogInRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val response = authService.login(request, httpRequest)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        authService.logout(request)
        return ResponseEntity.ok(ApiResponse.success())
    }
}