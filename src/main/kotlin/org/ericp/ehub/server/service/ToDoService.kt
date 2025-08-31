package org.ericp.ehub.server.service

import org.ericp.ehub.server.dto.ToDoDto
import org.ericp.ehub.server.entity.ToDo
import org.ericp.ehub.server.mapper.ToDoMapper
import org.ericp.ehub.server.repository.ToDoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class ToDoService(
    private val toDoRepository: ToDoRepository,
    private val toDoCategoryService: ToDoCategoryService,
    private val mapper: ToDoMapper
) {

    fun findAll(): List<ToDoDto> = toDoRepository.findAll().map { mapper.toDto(it)}

    fun findById(id: UUID): ToDoDto? = toDoRepository.findById(id)
        .map { mapper.toDto(it) }
        .orElse(null)

    fun create(toDoDto: ToDoDto): ToDoDto {
        return createToDoRecursively(toDoDto)
    }

    private fun createToDoRecursively(toDoDto: ToDoDto): ToDoDto {
        // Process categories: find existing ones by ID or create new ones
        val categories = toDoCategoryService.processCategories(toDoDto.categories ?: emptyList())

        // Process subtasks recursively first (bottom-up approach)
        val createdSubTasks: List<ToDoDto> = if (!toDoDto.subToDos.isNullOrEmpty()) {
            toDoDto.subToDos.map { subToDoDto ->
                // Recursively create each subtask
                createToDoRecursively(subToDoDto)
            }
        } else {
            emptyList()
        }

        // Convert the created subtask DTOs back to entities for the parent relationship
        val subTaskEntities = createdSubTasks.map { createdSubTaskDto ->
            // Fetch the saved entity from DB using the ID
            toDoRepository.findById(createdSubTaskDto.id!!).get()
        }

        // Create the main td with all processed subtasks
        val toDoEntity = mapper.toEntity(toDoDto, categories, subTaskEntities)
        val savedToDo = toDoRepository.save(toDoEntity)

        return mapper.toDto(savedToDo)
    }

    @Transactional
    fun update(id: UUID, updatedToDoDto: ToDoDto): ToDoDto? {
        return if (toDoRepository.existsById(id)) {
            // Process categories: find existing ones by ID or create new ones
            val categories = toDoCategoryService.processCategories(updatedToDoDto.categories ?: emptyList())

            val toDoEntity = mapper.toEntity(updatedToDoDto.copy(id = id), categories)
            val savedToDo = toDoRepository.save(toDoEntity)

            mapper.toDto(savedToDo)
        } else {
            null
        }
    }

    fun delete(id: UUID): Boolean {
        return if (toDoRepository.existsById(id)) {
            toDoRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun searchByTitle(title: String): List<ToDo> =
        toDoRepository.findByTitleContainingIgnoreCase(title)

    fun searchByDescription(description: String): List<ToDo> =
        toDoRepository.findByDescriptionContainingIgnoreCase(description)

    fun addSubtask(parentId: UUID, toDoDto: ToDoDto): ToDo? {
        val parentTodo = toDoRepository.findById(parentId).orElse(null)
        return if (parentTodo != null) {
            val categories = toDoCategoryService.processCategories(toDoDto.categories ?: emptyList())
            val subtaskEntity = mapper.toEntity(toDoDto, categories)
            val savedSubtask = toDoRepository.save(subtaskEntity)

            // Update the parent td to include the new subtask
            val updatedParent = parentTodo.copy(
                subToDos = parentTodo.subToDos + savedSubtask
            )
            toDoRepository.save(updatedParent)

            savedSubtask
        } else {
            null
        }
    }

    fun findSubToDos(parentId: UUID): List<ToDoDto> {
        val parent = toDoRepository.findById(parentId).orElse(null)
        return if (parent != null) {
            parent.subToDos.map { mapper.toDto(it) }
        } else {
            emptyList()
        }
    }

    fun findRootToDos(): List<ToDoDto> {
        // For now, return all todos since we no longer have a direct parent-child relationship
        // You might need to implement custom logic based on your business requirements
        return findAll()
    }

    fun addSubtaskDto(parentId: UUID, toDoDto: ToDoDto): ToDoDto? {
        val parentTodo = toDoRepository.findById(parentId).orElse(null)
        return if (parentTodo != null) {
            val categories = toDoCategoryService.processCategories(toDoDto.categories ?: emptyList())
            val subtaskEntity = mapper.toEntity(toDoDto, categories)
            val savedSubtask = toDoRepository.save(subtaskEntity)

            // Update the parent td to include the new subtask
            val updatedParent = parentTodo.copy(
                subToDos = parentTodo.subToDos + savedSubtask
            )
            toDoRepository.save(updatedParent)

            mapper.toDto(savedSubtask)
        } else {
            null
        }
    }
}
