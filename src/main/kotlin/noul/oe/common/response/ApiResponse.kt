package noul.oe.common.response

import noul.oe.common.exception.ErrorCode


data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null,
) {
    companion object {
        fun <T> success(data: T) : ApiResponse<T> {
            return ApiResponse(success = true, data = data)
        }

        fun <T> success() : ApiResponse<T> {
            return ApiResponse(success = true)
        }

        fun <T> error(errorCode: ErrorCode) : ApiResponse<T> {
            return ApiResponse(success = false, error = ApiError(errorCode.code, errorCode.message))
        }
    }
}

data class ApiError(
    val code: String,
    val message: String,
)