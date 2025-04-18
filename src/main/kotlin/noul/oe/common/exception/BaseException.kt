package noul.oe.common.exception

open class BaseException(
    val errorCode: ErrorCode,
    message: String? = null
) : RuntimeException(message ?: errorCode.message) {
    constructor(errorCode: ErrorCode) : this(errorCode, null)
}