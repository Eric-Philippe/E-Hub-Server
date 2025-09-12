package org.ericp.ehub.server.service

import org.ericp.ehub.server.dto.ToDoDto
import org.ericp.ehub.server.dto.ToDoRequest
import org.ericp.ehub.server.entity.State
import org.ericp.ehub.server.entity.ToDo
import org.ericp.ehub.server.mapper.ToDoMapper
import org.ericp.ehub.server.repository.ToDoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class ToDoService(
    private val toDoRepository: ToDoRepository,
    private val mapper: ToDoMapper
) {

    fun findAll(): List<ToDoDto> {
        val todos = toDoRepository.findAll()
        return mapper.toDtoTree(todos)
    }

    fun findById(id: UUID): ToDoDto {
        val todo = toDoRepository.findById(id)
            .orElseThrow { NoSuchElementException("ToDo with id=$id not found") }
        return mapper.toDto(todo)
    }


    fun create(request: ToDoRequest): ToDoDto {
        val parent = request.parentId?.let { pid ->
            toDoRepository.findById(pid).orElse(null)
        }

        val entity = mapper.toEntity(request, parent)
        val saved = toDoRepository.save(entity)
        return mapper.toDto(saved)
    }

    fun deleteRecursively(id: UUID) {
        val todo = toDoRepository.findById(id)
            .orElseThrow { NoSuchElementException("ToDo with id=$id not found") }
        deleteChildren(todo)
        toDoRepository.delete(todo)
    }

    private fun deleteChildren(todo: ToDo) {
        val children = toDoRepository.findByParentId(todo.id!!)
        children.forEach { deleteChildren(it) }
        toDoRepository.deleteAll(children)
    }

    fun update(id: UUID, request: ToDoRequest): ToDoDto {
        val todo = toDoRepository.findById(id)
            .orElseThrow { NoSuchElementException("ToDo with id=$id not found") }

        val parent = request.parentId?.let { pid ->
            if (pid == id) throw IllegalArgumentException("A ToDo cannot be its own parent")
            toDoRepository.findById(pid).orElse(null)
        }

        mapper.updateEntityFromRequest(todo, request, parent)
        val saved = toDoRepository.save(todo)
        return mapper.toDto(saved)
    }

    fun updateState(id: UUID, newState: State): ToDoDto {
        val todo = toDoRepository.findById(id)
            .orElseThrow { NoSuchElementException("ToDo with id=$id not found") }
        if (todo.state == newState) return mapper.toDto(todo)
        todo.state = newState
        todo.modified = LocalDateTime.now()
        toDoRepository.save(todo)
        propagateStateUp(todo)
        return mapper.toDto(todo)
    }

    fun getColorFromParent(parentId : UUID?): String {
        var currentId = parentId
        while (currentId != null) {
            val parent = toDoRepository.findById(currentId).orElse(null) ?: return "#FFFFFF"
            if (parent.color != null) {
                return parent.color!!
            }
            currentId = parent.parent?.id
        }
        return "#FFFFFF" // Default color if no ancestor has a color
    }

    private fun propagateStateUp(todo: ToDo) {
        val parent = todo.parent ?: return
        val siblings = toDoRepository.findByParentId(parent.id!!)
        val allDone = siblings.all { it.state == State.DONE }
        val anyInProgress = siblings.any { it.state == State.IN_PROGRESS }
        if (allDone && parent.state != State.DONE) {
            parent.state = State.DONE
            parent.modified = LocalDateTime.now()
            toDoRepository.save(parent)
            propagateStateUp(parent)
        } else if (anyInProgress && parent.state != State.IN_PROGRESS) {
            parent.state = State.IN_PROGRESS
            parent.modified = LocalDateTime.now()
            toDoRepository.save(parent)
            propagateStateUp(parent)
        }
    }
}
