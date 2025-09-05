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

    fun findByToBuyIdAndUrl(toBuyId: UUID, url: String): Optional<ToBuyLink>

    @Query("SELECT t.url, t.illustrationUrl FROM ToBuyLink t WHERE t.illustrationUrl IS NOT NULL")
    fun findAllUrlAndIllustrationUrl(): List<Array<Any>>

    @Modifying
    @Query("DELETE FROM ToBuyLink t WHERE t.toBuy.id = :toBuyId")
    fun deleteByToBuyId(toBuyId: UUID): Int
}
