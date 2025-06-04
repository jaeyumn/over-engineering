package noul.oe.support.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    // 도메인 모듈 예외
    @ExceptionHandler(DomainException::class)
    fun handleDomainException(e: DomainException): ResponseEntity<ErrorResponse> {
        val code = e.errorCode
        val message = e.message ?: code.message
        logger.info { "[${code.code}] $message" }
        return toResponse(code, message)
    }

    // Security 인증 실패 예외
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<ErrorResponse> {
        val code = CommonErrorCode.UNAUTHORIZED
        logger.info { "[${code.code}] ${code.message}" }
        return toResponse(code)
    }

    // Validation 예외
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val code = CommonErrorCode.INVALID_INPUT
        val fieldErrors = e.bindingResult.fieldErrors.joinToString {
            "${it.field} : ${it.defaultMessage ?: code.message}"
        }
        val message = fieldErrors.ifEmpty { code.message }
        logger.info { "[${code.code}] $message" }
        return toResponse(code, message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        val message = e.message ?: errorCode.message
        logger.error(e) { "[${errorCode.code}] Server Error: $message" }
        return toResponse(errorCode, message)
    }

    // 응답 포맷 공통 생성
    private fun toResponse(errorCode: ErrorCode, message: String = errorCode.message): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(errorCode.code, message)
        return ResponseEntity.status(errorCode.status).body(response)
    }
}