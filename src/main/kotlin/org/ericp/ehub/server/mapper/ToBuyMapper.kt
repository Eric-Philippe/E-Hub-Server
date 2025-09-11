package org.ericp.ehub.server.mapper

import org.ericp.ehub.server.dto.ToBuyDto
import org.ericp.ehub.server.dto.ToBuyLinkDto
import org.ericp.ehub.server.dto.ToBuyLinkUpdateDto
import org.ericp.ehub.server.dto.ToBuyUpdateDto
import org.ericp.ehub.server.entity.ToBuy
import org.ericp.ehub.server.entity.ToBuyCategory
import org.ericp.ehub.server.entity.ToBuyLink
import org.ericp.ehub.server.utils.IllustrationExtractor
import org.springframework.stereotype.Component

@Component
class ToBuyMapper(
    private val categoryMapper: ToBuyCategoryMapper,
    private val illustrationExtractor: IllustrationExtractor
) {

    fun toDto(entity: ToBuy): ToBuyDto {
        return ToBuyDto(
            id = entity.id,
            title = entity.title,
            created = entity.created,
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

    fun toEntity(dto: ToBuyUpdateDto, existing: ToBuy, categories: List<ToBuyCategory> = emptyList()): ToBuy {
        return ToBuy(
            id = dto.id ?: existing.id, // Let Hibernate manage ID generation for new entities
            title = dto.title ?: existing.title,
            description = dto.description ?: existing.description,
            criteria = dto.criteria ?: existing.criteria,
            bought = dto.bought ?: existing.bought,
            estimatedPrice = dto.estimatedPrice ?: existing.estimatedPrice,
            interest = dto.interest ?: existing.interest,
            tbCategories = categories.ifEmpty { existing.tbCategories },
            links = existing.links // Links will be handled separately
        )
    }

    fun toLinkDto(entity: ToBuyLink): ToBuyLinkDto {
        return ToBuyLinkDto(
            id = entity.id,
            url = entity.url,
            illustrationUrl = entity.illustrationUrl,
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
            illustrationUrl = illustrationExtractor.extract(dto.url),
            price = dto.price,
            favourite = dto.favourite,
            toBuy = toBuy,
            toBuyId = toBuyId
        )
    }

    fun toLinkEntity(dto: ToBuyLinkUpdateDto, toBuy: ToBuy? = null): ToBuyLink {
        // Cast dto to ToBuyLinkDto and reuse the existing method
        val toBuyLinkDto = ToBuyLinkDto(
            id = dto.id,
            url = dto.url ?: "",
            illustrationUrl = dto.illustrationUrl,
            price = dto.price,
            favourite = dto.favourite ?: false,
            toBuyId = toBuy?.id
        )
        return toLinkEntity(toBuyLinkDto, toBuy)
    }
}
