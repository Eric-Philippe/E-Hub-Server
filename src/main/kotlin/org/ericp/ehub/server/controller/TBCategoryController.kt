package org.ericp.ehub.server.controller

import org.ericp.ehub.server.dto.ToBuyCategoryDto
import org.ericp.ehub.server.service.ToBuyCategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/tobuy/categories")
@CrossOrigin
class TBCategoryController(
    private val categoryService: ToBuyCategoryService
) {

    @GetMapping
    fun getAllCategories(): List<ToBuyCategoryDto> = categoryService.findAll()

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: UUID): ResponseEntity<ToBuyCategoryDto> {
        return categoryService.findById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createCategory(@RequestBody category: ToBuyCategoryDto): ResponseEntity<ToBuyCategoryDto> {
        val created = categoryService.create(category)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun updateCategory(@PathVariable id: UUID, @RequestBody category: ToBuyCategoryDto): ResponseEntity<ToBuyCategoryDto> {
        return categoryService.update(id, category)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (categoryService.delete(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
