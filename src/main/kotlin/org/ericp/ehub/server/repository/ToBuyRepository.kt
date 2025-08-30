package org.ericp.ehub.server.repository

import org.ericp.ehub.server.entity.ToBuy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface ToBuyRepository : JpaRepository<ToBuy, UUID> {

    fun findByTitleContainingIgnoreCase(title: String): List<ToBuy>

    fun findByBoughtIsNull(): List<ToBuy>

    fun findByBoughtIsNotNull(): List<ToBuy>

    fun findByInterest(interest: String): List<ToBuy>

    @Query("SELECT t FROM ToBuy t WHERE t.estimatedPrice BETWEEN :minPrice AND :maxPrice")
    fun findByEstimatedPriceBetween(minPrice: Int, maxPrice: Int): List<ToBuy>
}
