package org.ericp.ehub.server.mapper

import org.ericp.ehub.server.dto.ToDoDto
import org.ericp.ehub.server.dto.ToDoRequest
import org.ericp.ehub.server.entity.ToDo
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ToDoMapper {

    fun toDto(todo: ToDo): ToDoDto =
        ToDoDto(
            id = todo.id!!,
            label = todo.label,
            state = todo.state,
            color = todo.color,
            created = todo.created.toString(),
            modified = todo.modified?.toString(),
            dueDate = todo.dueDate?.toString(),
            description = todo.description,
            parentId = todo.parent?.id,
            children = emptyList()
        )

    fun toDtoTree(todos: List<ToDo>): List<ToDoDto> {
        val byParent = todos.groupBy { it.parent?.id }

        fun buildNode(todo: ToDo): ToDoDto =
            toDto(todo).copy(
                children = byParent[todo.id]?.map { buildNode(it) } ?: emptyList()
            )

        return byParent[null]?.map { buildNode(it) } ?: emptyList()
    }

    fun toEntity(request: ToDoRequest, parent: ToDo?): ToDo =
        ToDo(
            label = request.label,
            state = request.state,
            color = request.color,
            dueDate = request.dueDate?.let { LocalDateTime.parse(it) },
            description = request.description,
            parent = parent
        )

    fun updateEntityFromRequest(entity: ToDo, request: ToDoRequest, parent: ToDo?) {
        entity.label = request.label
        entity.state = request.state
        entity.color = request.color
        entity.dueDate = request.dueDate?.let { LocalDateTime.parse(it) }
        entity.description = request.description
        entity.parent = parent
        entity.modified = LocalDateTime.now()
    }
}
