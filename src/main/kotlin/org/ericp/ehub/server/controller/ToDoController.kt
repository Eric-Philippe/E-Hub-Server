package org.ericp.ehub.server.controller

import org.ericp.ehub.server.dto.ToDoDto
import org.ericp.ehub.server.dto.ToDoDtoPartial
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
    fun getAllToDos(): List<ToDoDto> = toDoService.findAll()

    @GetMapping("/{id}")
    fun getToDoById(@PathVariable id: UUID): ResponseEntity<ToDoDto> {
        return toDoService.findById(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createToDo(@RequestBody toDo: ToDoDtoPartial): ResponseEntity<ToDoDto> {
        val created = toDoService.create(toDo)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun updateToDo(@PathVariable id: UUID, @RequestBody toDo: ToDoDto): ResponseEntity<ToDoDto> {
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

    @PutMapping("/bulk")
    fun bulkUpdate(@RequestBody toDos: List<ToDoDto>): ResponseEntity<List<ToDoDto>> {
        val updated = toDoService.bulkUpdate(toDos)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/bulk")
    fun bulkDelete(@RequestBody ids: List<UUID>): ResponseEntity<Void> {
        return if (toDoService.bulkDelete(ids)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
