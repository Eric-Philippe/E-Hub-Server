package org.ericp.ehub.server.controller

import org.ericp.ehub.server.entity.BannerMessage
import org.ericp.ehub.server.service.BannerMessageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/banner")
@CrossOrigin
class BannerMessageController(
    private val bannerMessageService: BannerMessageService
) {

    @GetMapping
    fun getAllMessages(): List<BannerMessage> = bannerMessageService.findAll()

    @GetMapping("/{id}")
    fun getMessageById(@PathVariable id: UUID): ResponseEntity<BannerMessage> {
        return bannerMessageService.findById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createMessage(@RequestBody message: BannerMessage): ResponseEntity<BannerMessage> {
        val created = bannerMessageService.create(message)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun updateMessage(@PathVariable id: UUID, @RequestBody message: BannerMessage): ResponseEntity<BannerMessage> {
        return bannerMessageService.update(id, message)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteMessage(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (bannerMessageService.delete(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/ordered")
    fun getMessagesOrderedByPriority(): List<BannerMessage> =
        bannerMessageService.findAllOrderedByPriority()

    @GetMapping("/priority")
    fun getMessagesByMaxPriority(@RequestParam maxPriority: Short): List<BannerMessage> =
        bannerMessageService.findByMaxPriority(maxPriority)

    @GetMapping("/highest-priority")
    fun getHighestPriorityMessages(): List<BannerMessage> =
        bannerMessageService.findHighestPriorityMessages()
}
