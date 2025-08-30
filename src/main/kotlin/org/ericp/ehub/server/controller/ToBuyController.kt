package org.ericp.ehub.server.controller

import org.ericp.ehub.server.dto.ToBuyDto
import org.ericp.ehub.server.entity.ToBuyLink
import org.ericp.ehub.server.service.ToBuyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/tobuy")
@CrossOrigin
class ToBuyController(
    private val toBuyService: ToBuyService
) {

    @GetMapping
    fun getAllToBuyItems(): List<ToBuyDto> = toBuyService.findAll()

    @GetMapping("/{id}")
    fun getToBuyById(@PathVariable id: UUID): ResponseEntity<ToBuyDto> {
        return toBuyService.findById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createToBuy(@RequestBody toBuy: ToBuyDto): ResponseEntity<ToBuyDto> {
        val created = toBuyService.create(toBuy)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun updateToBuy(@PathVariable id: UUID, @RequestBody toBuy: ToBuyDto): ResponseEntity<ToBuyDto> {
        return toBuyService.update(id, toBuy)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteToBuy(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (toBuyService.delete(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/{id}/buy")
    fun markAsBought(@PathVariable id: UUID): ResponseEntity<ToBuyDto> {
        return toBuyService.markAsBought(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/unbought")
    fun getUnboughtItems(): List<ToBuyDto> = toBuyService.findUnboughtItems()

    @GetMapping("/bought")
    fun getBoughtItems(): List<ToBuyDto> = toBuyService.findBoughtItems()

    @GetMapping("/search")
    fun searchByTitle(@RequestParam title: String): List<ToBuyDto> =
        toBuyService.searchByTitle(title)

    @GetMapping("/interest/{interest}")
    fun getByInterest(@PathVariable interest: String): List<ToBuyDto> =
        toBuyService.findByInterest(interest)

    @GetMapping("/price")
    fun getByPriceRange(
        @RequestParam minPrice: Int,
        @RequestParam maxPrice: Int
    ): List<ToBuyDto> = toBuyService.findByPriceRange(minPrice, maxPrice)

    @PostMapping("/{id}/links")
    fun addLink(@PathVariable id: UUID, @RequestBody link: ToBuyLink): ResponseEntity<ToBuyLink> {
        return toBuyService.addLinkToItem(id, link)?.let {
            ResponseEntity.status(HttpStatus.CREATED).body(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/{id}/links")
    fun getLinks(@PathVariable id: UUID): List<ToBuyLink> =
        toBuyService.getLinksForItem(id)
}
