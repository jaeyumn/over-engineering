package noul.oe.common.exception

import noul.oe.common.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(exception: BaseException): ResponseEntity<ApiResponse<Nothing>> {
        return generateErrorResponse(exception.errorCode)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = CommonErrorCode.INVALID_INPUT
        val fieldErrors = exception.bindingResult.fieldErrors.joinToString {
            "${it.field} : ${it.defaultMessage ?: errorCode.message}"
        }
        val message = fieldErrors.ifEmpty { errorCode.message }

        return generateErrorResponse(errorCode, message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        val message = exception.message ?: errorCode.message
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