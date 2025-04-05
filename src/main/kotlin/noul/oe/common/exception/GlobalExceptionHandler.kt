package noul.oe.common.exception

import noul.oe.common.response.ApiResponse
import noul.oe.user.exception.UserErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ApiResponse<Nothing>> {
        return generateErrorResponse(e.errorCode)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = UserErrorCode.INVALID_CREDENTIALS
        val message = e.message ?: errorCode.message
        return generateErrorResponse(errorCode, message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = CommonErrorCode.INVALID_INPUT
        val fieldErrors = e.bindingResult.fieldErrors.joinToString {
            "${it.field} : ${it.defaultMessage ?: errorCode.message}"
        }
        val message = fieldErrors.ifEmpty { errorCode.message }

        return generateErrorResponse(errorCode, message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        val message = e.message ?: errorCode.message
        return generateErrorResponse(errorCode, message)
    }

    private fun generateErrorResponse(errorCode: ErrorCode, message: String = errorCode.message): ResponseEntity<ApiResponse<Nothing>> {
        val dynamicErrorCode = object : ErrorCode {
            override val code: String = errorCode.code
            override val message: String = message
            override val status: HttpStatus = errorCode.status
        }
        return ResponseEntity.status(errorCode.status).body(ApiResponse.error(dynamicErrorCode))
    }
}