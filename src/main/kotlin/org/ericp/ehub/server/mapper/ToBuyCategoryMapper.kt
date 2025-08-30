package org.ericp.ehub.server.mapper

import org.ericp.ehub.server.dto.ToBuyCategoryDto
import org.ericp.ehub.server.entity.ToBuyCategory
import org.springframework.stereotype.Component

@Component
class ToBuyCategoryMapper {

    fun toDto(entity: ToBuyCategory): ToBuyCategoryDto {
        return ToBuyCategoryDto(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            color = entity.color
        )
    }

    fun toEntity(dto: ToBuyCategoryDto): ToBuyCategory {
        // Validate that we have either an ID or all required fields for creation
        if (dto.id == null) {
            // For new categories, all fields are required
            require(dto.name != null) { "Name is required for new categories" }
            require(dto.description != null) { "Description is required for new categories" }
            require(dto.color != null) { "Color is required for new categories" }
        }

        return ToBuyCategory(
            id = dto.id,
            name = dto.name ?: "", // This will only be used for new entities where name is validated above
            description = dto.description ?: "",
            color = dto.color ?: ""
        )
    }
}
