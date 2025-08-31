package org.ericp.ehub.server.mapper

import org.ericp.ehub.server.dto.ToDoCategoryDto
import org.ericp.ehub.server.entity.ToDoCategory
import org.ericp.ehub.server.utils.Utils
import org.springframework.stereotype.Component

@Component
class ToDoCategoryMapper {
    fun toDto(entity: ToDoCategory): ToDoCategoryDto {
        return ToDoCategoryDto(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            color = entity.color
        )
    }

    fun toEntity(dto: ToDoCategoryDto): ToDoCategory {
        // Validate that we have either an ID or all required fields for creation
        if (dto.id == null) {
            // For new categories, all fields are required
            require(dto.name != null) { "Name is required for new categories" }
            require(dto.description != null) { "Description is required for new categories" }
            // If color is null, generate a random color
            val color = dto.color ?: Utils.generateRandomColor()

            return ToDoCategory(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                color = color
            )
        }

        return ToDoCategory(
            id = dto.id,
            name = dto.name ?: "", // This will only be used for new entities where name is validated above
            description = dto.description ?: "",
            color = dto.color ?: ""
        )
    }
}