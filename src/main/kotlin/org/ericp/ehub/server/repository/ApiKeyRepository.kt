package org.ericp.ehub.server.repository

import org.ericp.ehub.server.entity.ApiKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApiKeyRepository : JpaRepository<ApiKey, UUID> {
    fun findByKeyHash(keyHash: String): ApiKey?
    fun findByKeyHashAndIsActiveTrue(keyHash: String): ApiKey?
}
