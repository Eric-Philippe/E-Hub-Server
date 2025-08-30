package org.ericp.ehub.server.repository

import org.ericp.ehub.server.entity.ToBuyLink
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ToBuyLinkRepository : JpaRepository<ToBuyLink, UUID> {

    fun findByToBuyId(toBuyId: UUID): List<ToBuyLink>

    fun findByFavouriteTrue(): List<ToBuyLink>

    fun findByUrlContainingIgnoreCase(url: String): List<ToBuyLink>

    fun findByPriceIsNotNull(): List<ToBuyLink>

    @Modifying
    @Query("DELETE FROM ToBuyLink t WHERE t.toBuyId = :toBuyId")
    fun deleteByToBuyId(toBuyId: UUID)
}
