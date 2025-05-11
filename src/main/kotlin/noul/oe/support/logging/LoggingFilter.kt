package noul.oe.support.logging

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

private val filterLogger = KotlinLogging.logger {}

@Component
class LoggingFilter : OncePerRequestFilter() {
    private val excludedPaths = listOf("/css/", "/js/", "/images/", "/favicon.ico")

    init {
        println("LoggingFilter initialized")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val uri = request.requestURI
            if (excludedUri(uri) || excludedHtml(request)) { // 정적 리소스 로깅 제외
                filterChain.doFilter(request, response)
                return
            }

            val traceId = generateTraceId()
            MDC.put("traceId", traceId)

            val method = request.method
            val query = request.queryString?.let { "?$it" } ?: ""
            val fullUrl = "$uri$query"
            filterLogger.trace { "[$method] $fullUrl request called" }
            filterChain.doFilter(request, response)
            filterLogger.trace { "[$method] $fullUrl request finished" }

        } finally {
            MDC.clear()
        }
    }

    private fun generateTraceId(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }

    private fun excludedUri(uri: String): Boolean {
        return excludedPaths.any { uri.startsWith(it) }
    }

    private fun excludedHtml(request: HttpServletRequest): Boolean {
        val accept = request.getHeader("Accept") ?: ""
        return accept.contains("text/html")
    }
}