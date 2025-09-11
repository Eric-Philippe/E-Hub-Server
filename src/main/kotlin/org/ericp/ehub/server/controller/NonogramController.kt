package org.ericp.ehub.server.controller

import org.ericp.ehub.server.entity.NonogramLog
import org.ericp.ehub.server.service.NonogramLogService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/nonogram")
@CrossOrigin
class NonogramController(
    private val nonogramLogService: NonogramLogService
) {

    @GetMapping("/logs")
    fun getAllLogs(): List<NonogramLog> = nonogramLogService.findAll()

    @PostMapping
    fun createGame(@RequestBody log: NonogramLog): ResponseEntity<NonogramLog> {
        val log = nonogramLogService.create(log)
        return ResponseEntity.status(HttpStatus.CREATED).body(log)
    }

    @PostMapping("/start")
    fun startNewGame(): ResponseEntity<NonogramLog> {
        val log = nonogramLogService.createAttempt()
        return ResponseEntity.status(HttpStatus.CREATED).body(log)
    }

    @GetMapping("/logs/between")
    fun getLogsBetween(
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ): List<NonogramLog> {
        val start = LocalDateTime.parse(startDate)
        val end = LocalDateTime.parse(endDate)
        return nonogramLogService.findGamesBetween(start, end)
    }

    @GetMapping("/logs/ordered")
    fun getLogsOrderedByDate(): List<NonogramLog> =
        nonogramLogService.findAllOrderedByDate()

    @GetMapping("/stats/average-duration")
    fun getAverageGameDuration(): ResponseEntity<Double> {
        return nonogramLogService.getAverageGameDuration()?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.noContent().build()
    }

    @GetMapping("/stats/total-games")
    fun getTotalGamesPlayed(): ResponseEntity<Int> {
        return ResponseEntity.ok(nonogramLogService.getTotalGamesPlayed())
    }
}
