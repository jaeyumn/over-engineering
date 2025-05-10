package noul.oe.support.exception

open class BaseException(
    val errorCode: ErrorCode,
    val logMessage: String? = null
) : RuntimeException(errorCode.message) {
    constructor(errorCode: ErrorCode) : this(errorCode, null)
}