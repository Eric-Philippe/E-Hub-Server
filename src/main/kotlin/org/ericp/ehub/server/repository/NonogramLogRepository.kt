package org.ericp.ehub.server.repository

import org.ericp.ehub.server.entity.NonogramLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface NonogramLogRepository : JpaRepository<NonogramLog, UUID> {

    fun findByStartedBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<NonogramLog>

    @Query("SELECT n FROM NonogramLog n ORDER BY n.started DESC")
    fun findAllOrderByStartedDesc(): List<NonogramLog>

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (ended - started))) FROM nonogram_logs", nativeQuery = true)
    fun findAverageGameDurationInSeconds(): Double?
}
