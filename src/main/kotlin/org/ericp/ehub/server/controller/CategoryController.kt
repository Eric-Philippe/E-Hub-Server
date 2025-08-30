package org.ericp.ehub.server.controller

import org.ericp.ehub.server.entity.ToBuyCategory
import org.ericp.ehub.server.entity.ToDoCategory
import org.ericp.ehub.server.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
class CategoryController(
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

    // ToDo Categories
    @GetMapping("/todo")
    fun getAllToDoCategories(): List<ToDoCategory> = categoryService.findAllToDoCategories()

    @GetMapping("/todo/{id}")
    fun getToDoCategoryById(@PathVariable id: UUID): ResponseEntity<ToDoCategory> {
        return categoryService.findToDoCategoryById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/todo")
    fun createToDoCategory(@RequestBody category: ToDoCategory): ResponseEntity<ToDoCategory> {
        val created = categoryService.createToDoCategory(category)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/todo/{id}")
    fun updateToDoCategory(@PathVariable id: UUID, @RequestBody category: ToDoCategory): ResponseEntity<ToDoCategory> {
        return categoryService.updateToDoCategory(id, category)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/todo/{id}")
    fun deleteToDoCategory(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (categoryService.deleteToDoCategory(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/todo/search")
    fun findToDoCategoryByName(@RequestParam name: String): ResponseEntity<ToDoCategory> {
        return categoryService.findToDoCategoryByName(name)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }
}
