package org.ericp.ehub.server.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ericp.ehub.server.service.ApiKeyService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ConditionalApiKeyAuthFilter(
    private val apiKeyService: ApiKeyService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestPath = request.requestURI
        val method = request.method

        // Always allow Swagger and health check endpoints
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response)
            return
        }

        // Check if this is the first API key creation request
        val isFirstApiKeyCreation = method == "POST" &&
            requestPath.startsWith("/api/keys") &&
            !apiKeyService.hasAnyApiKeys()

        if (isFirstApiKeyCreation) {
            // Allow unauthenticated creation of the first API key
            val authentication = UsernamePasswordAuthenticationToken("first-time-setup", null, emptyList())
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)
            return
        }

        // For all other requests, require API key authentication
        val apiKey = extractApiKey(request)

        if (apiKey == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("{\"error\": \"API key required\"}")
            response.contentType = "application/json"
            return
        }

        val validApiKey = apiKeyService.validateApiKey(apiKey)
        if (validApiKey == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("{\"error\": \"Invalid or expired API key\"}")
            response.contentType = "application/json"
            return
        }

        // Set authentication in security context
        val authentication = UsernamePasswordAuthenticationToken(validApiKey.name, null, emptyList())
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }

    private fun extractApiKey(request: HttpServletRequest): String? {
        // Try to get API key from Authorization header (Bearer token)
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7)
        }

        // Try to get API key from X-API-Key header
        val apiKeyHeader = request.getHeader("X-API-Key")
        if (apiKeyHeader != null) {
            return apiKeyHeader
        }

        // Try to get API key from query parameter
        return request.getParameter("apiKey")
    }

    private fun isPublicEndpoint(path: String): Boolean {
        val publicPaths = listOf(
            "/swagger-ui",
            "/v3/api-docs",
            "/v2/api-docs",
            "/webjars",
            "/actuator/health",
            "/actuator/info",
            "/api/health"
        )
        return publicPaths.any { path.startsWith(it) }
    }
}
