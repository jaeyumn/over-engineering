package noul.oe.common.exception

import org.springframework.http.HttpStatus

interface ErrorCode {
    val code: String
    val message: String
    val status: HttpStatus
}