package net.nuqta.projecto.autolog.core

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Filter that manages the request ID in the MDC.
 * If the X-Request-Id header is present, it will be used as the request ID.
 * Otherwise, a random UUID will be generated.
 * The request ID will be available in the MDC with the key "requestId".
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestIdFilter : Filter {

    companion object {
        const val REQUEST_ID_HEADER = "X-Request-Id"
        const val REQUEST_ID_MDC_KEY = "requestId"
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            val httpRequest = request as HttpServletRequest
            val requestId = httpRequest.getHeader(REQUEST_ID_HEADER) ?: UUID.randomUUID().toString()
            MDC.put(REQUEST_ID_MDC_KEY, requestId)

            chain.doFilter(request, response)
        } finally {
            MDC.remove(REQUEST_ID_MDC_KEY)
        }
    }
}
