package noul.oe.support.exception

import org.springframework.http.HttpStatus

interface ErrorCode {
    val status: HttpStatus
    val code: String
    val message: String
}