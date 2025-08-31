package org.ericp.ehub.server.mapper

import org.ericp.ehub.server.dto.ToDoDto
import org.ericp.ehub.server.entity.ToDo
import org.ericp.ehub.server.entity.ToDoCategory
import org.springframework.stereotype.Component

@Component
class ToDoMapper(
    private val categoryMapper: ToDoCategoryMapper
) {

    fun toDto(entity: ToDo): ToDoDto {
        return ToDoDto(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            categories = entity.categories.map { categoryMapper.toDto(it) },
            subToDos = entity.subToDos.map { toDto(it) }
        )
    }

    fun toEntity(dto: ToDoDto, categories: List<ToDoCategory> = emptyList(), subTasks: List<ToDo> = emptyList()): ToDo {
        return ToDo(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            categories = categories,
            subToDos = subTasks
        )
    }
}