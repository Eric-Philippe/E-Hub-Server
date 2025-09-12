package org.ericp.ehub.server.service

import org.ericp.ehub.server.dto.ToDoDto
import org.ericp.ehub.server.dto.ToDoDtoPartial
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
    private val mapper: ToDoMapper
) {
    fun findAll(): List<ToDoDto> {
        // Get all ToDos from database - use simple findAll to avoid lazy loading issues
        val allToDos = toDoRepository.findAll()

        // Build the tree structure manually
        // Create a map of parent ID to children for quick lookup
        val childrenByParentId = mutableMapOf<UUID, MutableList<ToDo>>()
        val rootToDos = mutableListOf<ToDo>()

        // First pass: categorize all ToDos
        allToDos.forEach { todo ->
            if (todo.parent == null) {
                rootToDos.add(todo)
            } else {
                todo.parent.id?.let { parentId ->
                    childrenByParentId.computeIfAbsent(parentId) { mutableListOf() }.add(todo)
                }
            }
        }

        // Function to recursively build ToDoDto with children
        fun buildToDtoWithChildren(entity: ToDo): ToDoDto {
            val children = childrenByParentId[entity.id]?.map { child ->
                buildToDtoWithChildren(child)
            } ?: emptyList()

            val parent = entity.parent?.let { parentEntity ->
                ToDoDto(
                    id = parentEntity.id,
                    label = parentEntity.label,
                    state = parentEntity.state,
                    color = parentEntity.color,
                    created = parentEntity.created,
                    modified = parentEntity.modified,
                    dueDate = parentEntity.dueDate,
                    description = parentEntity.description,
                    parent = null, // Don't include grandparent
                    children = null // Don't include parent's other children
                )
            }

            return ToDoDto(
                id = entity.id,
                label = entity.label,
                state = entity.state,
                color = entity.color,
                created = entity.created,
                modified = entity.modified,
                dueDate = entity.dueDate,
                description = entity.description,
                parent = parent,
                children = children
            )
        }

        // Convert root ToDos to DTOs with their full tree structure
        return rootToDos.map { buildToDtoWithChildren(it) }
    }

    fun findById(id: UUID): ToDoDto? = toDoRepository.findByIdWithTree(id)?.let { mapper.toDto(it) }

    fun create(toDoDto: ToDoDto): ToDoDto {
        val entity = mapper.toEntity(toDoDto)
        val saved = toDoRepository.save(entity)
        return mapper.toDto(saved)
    }

    fun create(toDoDtoPartial: ToDoDtoPartial): ToDoDto {
        val parent = toDoDtoPartial.parentId?.let { toDoRepository.findById(it).orElse(null) }
        val entity = mapper.toEntityPartial(toDoDtoPartial, parent)
        val saved = toDoRepository.save(entity)
        return mapper.toDto(saved)
    }

    fun update(id: UUID, toDoDto: ToDoDto): ToDoDto? {
        return if (toDoRepository.existsById(id)) {
            val entity = mapper.toEntity(toDoDto.copy(id = id))
            val saved = toDoRepository.save(entity)
            mapper.toDto(saved)
        } else null
    }

    fun delete(id: UUID): Boolean {
        return if (toDoRepository.existsById(id)) {
            toDoRepository.deleteById(id)
            true
        } else false
    }

    fun bulkUpdate(toDos: List<ToDoDto>): List<ToDoDto> {
        val entities = toDos.map { mapper.toEntity(it) }
        val saved = toDoRepository.saveAll(entities)
        return saved.map { mapper.toDto(it) }
    }

    fun bulkDelete(ids: List<UUID>): Boolean {
        val existing = toDoRepository.findAllById(ids)
        if (existing.isNotEmpty()) {
            toDoRepository.deleteAll(existing)
            return true
        }
        return false
    }
}
