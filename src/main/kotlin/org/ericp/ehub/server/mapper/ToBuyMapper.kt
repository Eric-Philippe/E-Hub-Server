package org.ericp.ehub.server.mapper

import org.ericp.ehub.server.dto.ToBuyDto
import org.ericp.ehub.server.dto.ToBuyLinkDto
import org.ericp.ehub.server.entity.ToBuy
import org.ericp.ehub.server.entity.ToBuyCategory
import org.ericp.ehub.server.entity.ToBuyLink
import org.springframework.stereotype.Component

@Component
class ToBuyMapper(
    private val categoryMapper: ToBuyCategoryMapper
) {

    fun toDto(entity: ToBuy): ToBuyDto {
        return ToBuyDto(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            criteria = entity.criteria,
            bought = entity.bought,
            estimatedPrice = entity.estimatedPrice,
            interest = entity.interest,
            categories = entity.tbCategories.map { categoryMapper.toDto(it) },
            links = entity.links.map { toLinkDto(it) }
        )
    }

    fun toEntity(dto: ToBuyDto, categories: List<ToBuyCategory> = emptyList()): ToBuy {
        return ToBuy(
            id = dto.id, // Let Hibernate manage ID generation for new entities
            title = dto.title,
            description = dto.description,
            criteria = dto.criteria,
            bought = dto.bought,
            estimatedPrice = dto.estimatedPrice,
            interest = dto.interest,
            tbCategories = categories,
            links = emptyList() // Links will be handled separately
        )
    }

    fun toLinkDto(entity: ToBuyLink): ToBuyLinkDto {
        return ToBuyLinkDto(
            id = entity.id,
            url = entity.url,
            price = entity.price,
            favourite = entity.favourite,
            toBuyId = entity.toBuyId
        )
    }

    fun toLinkEntity(dto: ToBuyLinkDto, toBuy: ToBuy? = null): ToBuyLink {
        val toBuyId = when {
            dto.toBuyId != null -> dto.toBuyId
            toBuy?.id != null -> toBuy.id
            else -> throw IllegalArgumentException("Cannot create link without a valid ToBuy ID")
        }

        return ToBuyLink(
            id = dto.id, // Let Hibernate manage ID generation for new entities
            url = dto.url,
            price = dto.price,
            favourite = dto.favourite,
            toBuy = toBuy,
            toBuyId = toBuyId
        )
    }
}
