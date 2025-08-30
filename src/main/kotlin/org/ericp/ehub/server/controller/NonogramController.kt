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

    @GetMapping("/logs/{id}")
    fun getLogById(@PathVariable id: UUID): ResponseEntity<NonogramLog> {
        return nonogramLogService.findById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun startGame(@RequestBody log: NonogramLog): ResponseEntity<NonogramLog> {
        val log = nonogramLogService.create(log)
        return ResponseEntity.status(HttpStatus.CREATED).body(log)
    }

    @PatchMapping("/end/{id}")
    fun endGame(@PathVariable id: UUID): ResponseEntity<NonogramLog> {
        return nonogramLogService.endGame(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
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

    @DeleteMapping("/logs/{id}")
    fun deleteLog(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (nonogramLogService.delete(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
