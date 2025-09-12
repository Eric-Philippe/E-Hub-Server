package org.ericp.ehub.server.controller

import org.ericp.ehub.server.dto.ToDoDto
import org.ericp.ehub.server.dto.ToDoRequest
import org.ericp.ehub.server.entity.State
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
        return toDoService.findById(id).let {
            ResponseEntity.ok(it)
        }
    }

    @PostMapping
    fun createToDo(@RequestBody toDo: ToDoRequest): ResponseEntity<ToDoDto> {
        if (toDo.color == null) {
            // Root to-do must have a color
            if (toDo.parentId == null)
                return ResponseEntity.badRequest().build()

            // Inherit color from the highest ancestor with a color
            val inheritedColor: String = toDoService.getColorFromParent(toDo.parentId)
            toDo.color = inheritedColor
        }
        val created = toDoService.create(toDo)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun updateToDo(@PathVariable id: UUID, @RequestBody toDo: ToDoRequest): ResponseEntity<ToDoDto> {
        return toDoService.update(id, toDo).let {
            ResponseEntity.ok(it)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteToDo(@PathVariable id: UUID): ResponseEntity<Void> {
        return try {
            toDoService.deleteRecursively(id)
            ResponseEntity.noContent().build()
        } catch (_: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/{id}/state")
    fun updateToDoState(@PathVariable id: UUID, @RequestBody body: Map<String, String>): ResponseEntity<ToDoDto> {
        val stateStr = body["state"] ?: return ResponseEntity.badRequest().build()
        val state = try { State.valueOf(stateStr) } catch (_: Exception) {
            return ResponseEntity.badRequest().build()
        }
        val updated = toDoService.updateState(id, state)
        return ResponseEntity.ok(updated)
    }
}
