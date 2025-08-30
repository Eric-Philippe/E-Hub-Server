package org.ericp.ehub.server.repository

import org.ericp.ehub.server.entity.BannerMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BannerMessageRepository : JpaRepository<BannerMessage, UUID> {

    fun findByOrderByPriorityAsc(): List<BannerMessage>

    fun findByPriorityLessThanEqual(maxPriority: Short): List<BannerMessage>

    @Query("SELECT b FROM BannerMessage b WHERE b.priority = (SELECT MIN(b2.priority) FROM BannerMessage b2)")
    fun findHighestPriorityMessages(): List<BannerMessage>
}
