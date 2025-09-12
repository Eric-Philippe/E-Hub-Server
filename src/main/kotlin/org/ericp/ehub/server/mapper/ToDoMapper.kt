package org.ericp.ehub.server.mapper

import org.ericp.ehub.server.dto.ToDoDto
import org.ericp.ehub.server.dto.ToDoDtoPartial
import org.ericp.ehub.server.entity.ToDo
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class ToDoMapper {
    fun toDto(entity: ToDo): ToDoDto {
        return ToDoDto(
            id = entity.id,
            label = entity.label,
            state = entity.state,
            color = entity.color,
            created = entity.created,
            modified = entity.modified,
            dueDate = entity.dueDate,
            description = entity.description,
            // Only map the immediate parent, do not recurse
            parent = entity.parent?.let {
                ToDoDto(
                    id = it.id,
                    label = it.label,
                    state = it.state,
                    color = it.color,
                    created = it.created,
                    modified = it.modified,
                    dueDate = it.dueDate,
                    description = it.description,
                    parent = null, // Do not map parent's parent
                    children = null // Do not map parent's children
                )
            },
            // Map children recursively, using a safe copy to avoid concurrent modification
            children = try {
                entity.children.takeIf { it.isNotEmpty() }?.let { children ->
                    // Create a defensive copy to avoid ConcurrentModificationException
                    ArrayList(children).map { toDto(it) }
                } ?: emptyList()
            } catch (e: Exception) {
                // If there's any issue accessing children, return empty list
                emptyList()
            }
        )
    }

    fun toEntity(dto: ToDoDto, parent: ToDo? = null): ToDo {
        return ToDo(
            id = dto.id,
            label = dto.label,
            state = dto.state,
            color = dto.color,
            created = dto.created ?: LocalDateTime.now(),
            modified = dto.modified,
            dueDate = dto.dueDate,
            description = dto.description,
            parent = parent ?: dto.parent?.let { toEntity(it) },
            children = dto.children?.map { toEntity(it) }?.toMutableSet() ?: mutableSetOf()
        )
    }

    fun toEntityPartial(dto: ToDoDtoPartial, parent: ToDo? = null): ToDo {
        return ToDo(
            id = dto.id,
            label = dto.label,
            state = dto.state,
            color = dto.color,
            created = dto.created,
            modified = dto.modified,
            dueDate = dto.dueDate,
            description = dto.description,
            parent = parent,
            children = mutableSetOf()
        )
    }

    fun toPartialDto(entity: ToDo): ToDoDtoPartial {
        return ToDoDtoPartial(
            id = entity.id,
            label = entity.label,
            state = entity.state,
            color = entity.color,
            created = entity.created,
            modified = entity.modified,
            dueDate = entity.dueDate,
            description = entity.description,
            parentId = entity.parent?.id
        )
    }
}
