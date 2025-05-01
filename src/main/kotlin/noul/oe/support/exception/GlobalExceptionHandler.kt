package noul.oe.support.exception

import noul.oe.user.exception.UserErrorCode
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ErrorResponse> {
        return generateErrorResponse(e.errorCode)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<ErrorResponse> {
        val errorCode = UserErrorCode.INVALID_CREDENTIALS
        val message = e.message ?: errorCode.message
        return generateErrorResponse(errorCode, message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.INVALID_INPUT
        val fieldErrors = e.bindingResult.fieldErrors.joinToString {
            "${it.field} : ${it.defaultMessage ?: errorCode.message}"
        }
        val message = fieldErrors.ifEmpty { errorCode.message }

        return generateErrorResponse(errorCode, message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        val message = e.message ?: errorCode.message
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