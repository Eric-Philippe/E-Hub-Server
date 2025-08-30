package org.ericp.ehub.server.controller

import org.ericp.ehub.server.entity.ToDo
import org.ericp.ehub.server.service.ToDoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/todo")
@CrossOrigin
class ToDoController(
    private val toDoService: ToDoService
) {

    @GetMapping
    fun getAllToDos(): List<ToDo> = toDoService.findAll()

    @GetMapping("/{id}")
    fun getToDoById(@PathVariable id: UUID): ResponseEntity<ToDo> {
        return toDoService.findById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createToDo(@RequestBody toDo: ToDo): ResponseEntity<ToDo> {
        val created = toDoService.create(toDo)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun updateToDo(@PathVariable id: UUID, @RequestBody toDo: ToDo): ResponseEntity<ToDo> {
        return toDoService.update(id, toDo)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteToDo(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (toDoService.delete(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    fun searchByTitle(@RequestParam title: String): List<ToDo> =
        toDoService.searchByTitle(title)

    @GetMapping("/search/description")
    fun searchByDescription(@RequestParam description: String): List<ToDo> =
        toDoService.searchByDescription(description)
}
