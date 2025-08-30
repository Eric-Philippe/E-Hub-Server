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

    fun endGame(id: UUID): NonogramLog? {
        return findById(id)?.let { log ->
            nonogramLogRepository.save(log.copy(ended = LocalDateTime.now()))
        }
    }

    fun findGamesBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<NonogramLog> =
        nonogramLogRepository.findByStartedBetween(startDate, endDate)

    fun findAllOrderedByDate(): List<NonogramLog> =
        nonogramLogRepository.findAllOrderByStartedDesc()

    fun getAverageGameDuration(): Double? =
        nonogramLogRepository.findAverageGameDurationInSeconds()

    fun delete(id: UUID): Boolean {
        return if (nonogramLogRepository.existsById(id)) {
            nonogramLogRepository.deleteById(id)
            true
        } else false
    }
}
