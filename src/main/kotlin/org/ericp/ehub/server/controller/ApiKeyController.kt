package org.ericp.ehub.server.controller

import org.ericp.ehub.server.service.ApiKeyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/keys")
@CrossOrigin
class ApiKeyController(
    private val apiKeyService: ApiKeyService
) {

    data class CreateApiKeyRequest(
        val name: String,
        val expirationDays: Long? = null
    )

    data class ApiKeyResponse(
        val id: UUID,
        val name: String,
        val isActive: Boolean,
        val createdAt: String,
        val lastUsedAt: String?,
        val expiresAt: String?,
        val key: String? = null // Only returned when creating a new key
    )

    @PostMapping
    fun createApiKey(@RequestBody request: CreateApiKeyRequest): ResponseEntity<ApiKeyResponse> {
        val (plainKey, apiKey) = apiKeyService.generateApiKey(request.name, request.expirationDays)

        val response = ApiKeyResponse(
            id = apiKey.id!!,
            name = apiKey.name,
            isActive = apiKey.isActive,
            createdAt = apiKey.createdAt.toString(),
            lastUsedAt = apiKey.lastUsedAt?.toString(),
            expiresAt = apiKey.expiresAt?.toString(),
            key = plainKey // IMPORTANT: This is the only time the key is returned
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getAllApiKeys(): List<ApiKeyResponse> {
        return apiKeyService.getAllApiKeys().map { apiKey ->
            ApiKeyResponse(
                id = apiKey.id!!,
                name = apiKey.name,
                isActive = apiKey.isActive,
                createdAt = apiKey.createdAt.toString(),
                lastUsedAt = apiKey.lastUsedAt?.toString(),
                expiresAt = apiKey.expiresAt?.toString()
            )
        }
    }

    @DeleteMapping("/{keyId}")
    fun revokeApiKey(@PathVariable keyId: UUID): ResponseEntity<Map<String, String>> {
        val revoked = apiKeyService.revokeApiKey(keyId)

        return if (revoked) {
            ResponseEntity.ok(mapOf("message" to "API key revoked successfully"))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
