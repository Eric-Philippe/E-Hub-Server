package org.ericp.ehub.server.repository

import org.ericp.ehub.server.entity.ToBuyCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ToBuyCategoryRepository : JpaRepository<ToBuyCategory, UUID> {

    fun findByNameIgnoreCase(name: String): ToBuyCategory?

    fun findByColor(color: String): List<ToBuyCategory>

    fun findByNameContainingIgnoreCase(name: String): List<ToBuyCategory>
}
