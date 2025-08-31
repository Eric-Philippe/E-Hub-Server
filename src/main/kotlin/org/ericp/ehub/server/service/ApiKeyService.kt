package org.ericp.ehub.server.service

import org.ericp.ehub.server.entity.ApiKey
import org.ericp.ehub.server.repository.ApiKeyRepository
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*

@Service
class ApiKeyService(
    private val apiKeyRepository: ApiKeyRepository
) {

    private val secureRandom = SecureRandom()

    /**
     * Generates a new API key and stores its hash in the database
     * @param name A descriptive name for the API key
     * @param expirationDays Number of days until expiration (null for no expiration)
     * @return Pair of (plaintext key, stored entity) - IMPORTANT: plaintext key should be shown only once
     */
    fun generateApiKey(name: String, expirationDays: Long? = null): Pair<String, ApiKey> {
        val plainKey = generateSecureKey()
        val keyHash = hashKey(plainKey)

        val expiresAt = expirationDays?.let { LocalDateTime.now().plusDays(it) }

        val apiKey = ApiKey(
            keyHash = keyHash,
            name = name,
            expiresAt = expiresAt
        )

        val savedKey = apiKeyRepository.save(apiKey)
        return Pair(plainKey, savedKey)
    }

    /**
     * Validates an API key
     * @param plainKey The plaintext API key to validate
     * @return The ApiKey entity if valid, null otherwise
     */
    fun validateApiKey(plainKey: String): ApiKey? {
        val keyHash = hashKey(plainKey)
        val apiKey = apiKeyRepository.findByKeyHashAndIsActiveTrue(keyHash)

        return apiKey?.let {
            // Check if key is expired
            if (it.expiresAt != null && it.expiresAt.isBefore(LocalDateTime.now())) {
                null
            } else {
                // Update last used timestamp
                it.lastUsedAt = LocalDateTime.now()
                apiKeyRepository.save(it)
                it
            }
        }
    }

    /**
     * Revokes an API key by setting it to inactive
     */
    fun revokeApiKey(keyId: UUID): Boolean {
        return apiKeyRepository.findById(keyId).map { apiKey ->
            val updatedKey = apiKey.copy(isActive = false)
            apiKeyRepository.save(updatedKey)
            true
        }.orElse(false)
    }

    /**
     * Lists all API keys (without revealing the actual keys)
     */
    fun getAllApiKeys(): List<ApiKey> = apiKeyRepository.findAll()

    /**
     * Checks if any API keys exist in the database
     */
    fun hasAnyApiKeys(): Boolean = apiKeyRepository.count() > 0

    /**
     * Generates a cryptographically secure random API key
     */
    private fun generateSecureKey(): String {
        val bytes = ByteArray(32) // 256 bits
        secureRandom.nextBytes(bytes)
        return "ehub_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    /**
     * Hashes an API key using SHA-256
     */
    private fun hashKey(key: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(key.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(hashedBytes)
    }
}
