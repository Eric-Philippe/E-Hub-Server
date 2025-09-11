package org.ericp.ehub.server.controller

import org.ericp.ehub.server.service.ApiKeyService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/health")
@CrossOrigin
class HealthController(
    private val apiKeyService: ApiKeyService
) {

    @Value("\${spring.application.name:EHub-Server}")
    private lateinit var applicationName: String

    @GetMapping
    fun getHealth(request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val isConnected = checkApiKeyAuthentication(request)

        val healthInfo = mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "application" to applicationName,
            "connected" to isConnected
        )
        return ResponseEntity.ok(healthInfo)
    }

    @GetMapping("/info")
    fun getInfo(): ResponseEntity<Map<String, Any>> {
        val appInfo = mapOf(
            "name" to applicationName,
            "version" to "0.0.1-SNAPSHOT",
            "description" to "EHub Server - Personal Hub API",
            "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "build" to mapOf(
                "kotlin" to "1.9.25",
                "springBoot" to "3.5.5",
                "java" to "21"
            )
        )
        return ResponseEntity.ok(appInfo)
    }

    @GetMapping("/version")
    fun getVersion(): ResponseEntity<Map<String, String>> {
        val versionInfo = mapOf(
            "version" to "0.0.1-SNAPSHOT",
            "buildTime" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        return ResponseEntity.ok(versionInfo)
    }

    private fun checkApiKeyAuthentication(request: HttpServletRequest): Boolean {
        // Check if there's an authenticated user in the security context
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated && authentication.name != "anonymousUser") {
            return true
        }

        // Also check directly for API key in headers/params
        val apiKey = extractApiKey(request)
        if (apiKey != null) {
            val validApiKey = apiKeyService.validateApiKey(apiKey)
            return validApiKey != null
        }

        return false
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
}
