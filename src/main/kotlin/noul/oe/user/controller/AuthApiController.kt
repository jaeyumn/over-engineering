package noul.oe.user.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
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
class AuthApiController(
    private val authService: AuthService
) {
    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: UserLogInRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<UserResponse> {
        val response = authService.login(request, httpRequest)
        return ResponseEntity.ok(response)
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Void> {
        authService.logout(request)
        return ResponseEntity.noContent().build()
    }
}