package org.ericp.ehub.server.service

import org.ericp.ehub.server.entity.NonogramLog
import org.ericp.ehub.server.repository.NonogramLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class NonogramLogService(
    private val nonogramLogRepository: NonogramLogRepository
) {

    fun findAll(): List<NonogramLog> = nonogramLogRepository.findAll()

    fun findById(id: UUID): NonogramLog? = nonogramLogRepository.findById(id).orElse(null)

    fun create(log: NonogramLog): NonogramLog = nonogramLogRepository.save(log)

    fun createAttempt(): NonogramLog {
        val log = NonogramLog(
            started = LocalDateTime.now(),
            ended = null
        )
        return nonogramLogRepository.save(log)
    }

    fun findGamesBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<NonogramLog> =
        nonogramLogRepository.findByStartedBetween(startDate, endDate)

    fun findAllOrderedByDate(): List<NonogramLog> =
        nonogramLogRepository.findAllOrderByStartedDesc()

    fun getAverageGameDuration(): Double? =
        nonogramLogRepository.findAverageGameDurationInSeconds()
}
