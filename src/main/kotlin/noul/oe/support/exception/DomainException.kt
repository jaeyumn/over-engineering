package noul.oe.support.exception

open class DomainException(
    val errorCode: ErrorCode,
    detail: String? = null
) : RuntimeException(
    if (!detail.isNullOrBlank()) "${errorCode.message} : $detail"
    else errorCode.message
)