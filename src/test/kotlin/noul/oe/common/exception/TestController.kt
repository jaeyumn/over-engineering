package noul.oe.common.exception

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}