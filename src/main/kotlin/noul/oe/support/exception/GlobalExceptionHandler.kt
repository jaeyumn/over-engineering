package noul.oe.support.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import noul.oe.core.user1.exception.UserErrorCode
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ErrorResponse> {
        logger.info { "[${e.errorCode.code}] ${e.logMessage}" }
        return generateErrorResponse(e.errorCode)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<ErrorResponse> {
        val errorCode = UserErrorCode.INVALID_CREDENTIALS
        val message = e.message ?: errorCode.message
        logger.info { "[${errorCode.code}] Login failed: $message" }
        return generateErrorResponse(errorCode, message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.INVALID_INPUT
        val fieldErrors = e.bindingResult.fieldErrors.joinToString {
            "${it.field} : ${it.defaultMessage ?: errorCode.message}"
        }
        val message = fieldErrors.ifEmpty { errorCode.message }
        logger.info { "[${errorCode.code}] $message" }
        return generateErrorResponse(errorCode, message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        val message = e.message ?: errorCode.message
        logger.error(e) { "[${errorCode.code}] Server Error: $message" }
        return generateErrorResponse(errorCode, message)
    }

    private fun generateErrorResponse(
        errorCode: ErrorCode,
        message: String = errorCode.message
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(errorCode.code, message)
        return ResponseEntity.status(errorCode.status).body(response)
    }
}