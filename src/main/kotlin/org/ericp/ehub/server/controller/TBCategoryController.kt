package org.ericp.ehub.server.controller

import org.ericp.ehub.server.entity.ToBuyCategory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import io.swagger.v3.oas.annotations.tags.Tag
import org.ericp.ehub.server.repository.ToBuyCategoryRepository
import org.ericp.ehub.server.service.CategoryService
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.UUID

class TBCategoryController(
    private val categoryService: CategoryService
) {

    // ToBuy Categories
    @GetMapping("/tobuy")
    fun getAllToBuyCategories(): List<ToBuyCategory> = categoryService.findAllToBuyCategories()

    @GetMapping("/tobuy/{id}")
    fun getToBuyCategoryById(@PathVariable id: UUID): ResponseEntity<ToBuyCategory> {
        return categoryService.findToBuyCategoryById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/tobuy")
    fun createToBuyCategory(@RequestBody category: ToBuyCategory): ResponseEntity<ToBuyCategory> {
        val created = categoryService.createToBuyCategory(category)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/tobuy/{id}")
    fun updateToBuyCategory(@PathVariable id: UUID, @RequestBody category: ToBuyCategory): ResponseEntity<ToBuyCategory> {
        return categoryService.updateToBuyCategory(id, category)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/tobuy/{id}")
    fun deleteToBuyCategory(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (categoryService.deleteToBuyCategory(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/tobuy/search")
    fun findToBuyCategoryByName(@RequestParam name: String): ResponseEntity<ToBuyCategory> {
        return categoryService.findToBuyCategoryByName(name)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }
}