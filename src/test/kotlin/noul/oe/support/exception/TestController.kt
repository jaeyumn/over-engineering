package noul.oe.support.exception

import jakarta.validation.Valid
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/test")
class TestController {
    @GetMapping("/base-exception")
    fun throwBaseException() {
        throw BaseException(CommonErrorCode.INVALID_INPUT)
    }

    @PostMapping("/validation-exception")
    fun throwValidationException(@Valid @RequestBody request: TestRequest) {
        // @Valid 검증 실패 후 MethodArgumentNotValidException 발생
    }

    @GetMapping("/internal-exception")
    fun throwGeneralException() {
        throw RuntimeException("테스트 오류 발생")
    }

    @GetMapping("/credentials-exception")
    fun throwCredentialsException() {
        throw BadCredentialsException("아이디 또는 비밀번호가 올바르지 않음")
    }
}