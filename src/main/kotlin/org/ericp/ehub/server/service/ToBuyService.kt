package org.ericp.ehub.server.service

import org.ericp.ehub.server.dto.ToBuyDto
import org.ericp.ehub.server.dto.ToBuyUpdateDto
import org.ericp.ehub.server.entity.ToBuyLink
import org.ericp.ehub.server.mapper.ToBuyMapper
import org.ericp.ehub.server.repository.ToBuyRepository
import org.ericp.ehub.server.repository.ToBuyLinkRepository
import org.ericp.ehub.server.utils.IllustrationExtractor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class ToBuyService(
    private val toBuyRepository: ToBuyRepository,
    private val toBuyLinkRepository: ToBuyLinkRepository,
    private val toBuyCategoryService: ToBuyCategoryService,
    private val mapper: ToBuyMapper,
    private val illustrationExtractor: IllustrationExtractor
) {

    fun findAll(): List<ToBuyDto> = toBuyRepository.findAll().map { mapper.toDto(it) }

    fun findById(id: UUID): ToBuyDto? = toBuyRepository.findById(id)
        .map { mapper.toDto(it) }
        .orElse(null)

    fun create(toBuyDto: ToBuyDto): ToBuyDto {
        // Process categories: find existing ones by ID or create new ones
        val categories = toBuyCategoryService.processCategories(toBuyDto.categories)

        val toBuyEntity = mapper.toEntity(toBuyDto, categories)
        val savedToBuy = toBuyRepository.save(toBuyEntity)

        // Save links if provided
        val savedLinks = toBuyDto.links.map { linkDto ->
            val linkEntity = mapper.toLinkEntity(linkDto, savedToBuy)
            toBuyLinkRepository.save(linkEntity)
        }

        return mapper.toDto(savedToBuy.copy(links = savedLinks))
    }

    @Transactional
    fun update(id: UUID, updatedToBuyDto: ToBuyUpdateDto): ToBuyDto? {
        val currentToBuy = toBuyRepository.findById(id)

        return if (!currentToBuy.isEmpty) {
            // Process categories: find existing ones by ID or create new ones
            val categories = toBuyCategoryService.processCategories(updatedToBuyDto.categories)

            val toBuyEntity = mapper.toEntity(updatedToBuyDto.copy(id = id), currentToBuy.get(), categories)
            val savedToBuy = toBuyRepository.save(toBuyEntity)

            val updatedLinks = mutableListOf<ToBuyLink>()
            if (updatedToBuyDto.links.isEmpty()) {
                // If links is empty, delete all current links for this toBuy
                toBuyLinkRepository.deleteByToBuyId(id)
            } else {
                /**
                 * If id or toBuyId is provided, update existing link
                 * If no id or toBuyId, still try to find with toBuyEntity id and url
                 * If found, update it
                 * If not found, create new one
                 * If link is missing from the before update list, delete it
                 */
                val existingLinks = toBuyLinkRepository.findByToBuyId(id).associateBy { it.id }
                for (linkDto in updatedToBuyDto.links) {
                    val linkEntity = if (linkDto.id != null && existingLinks.containsKey(linkDto.id)) {
                        // Update existing link by ID
                        val existingLink = existingLinks[linkDto.id]!!
                        existingLink.copy(
                            url = linkDto.url ?: existingLink.url,
                            price = linkDto.price ?: existingLink.price,
                            favourite = linkDto.favourite ?: existingLink.favourite,
                            illustrationUrl = illustrationExtractor.extract(linkDto.url ?: existingLink.url, false) ?: existingLink.illustrationUrl
                        )
                    } else {
                        // Try to find by URL and toBuyId
                        val foundLink = toBuyLinkRepository.findByToBuyIdAndUrl(id, linkDto.url ?: "")
                        if (foundLink.isPresent) {
                            // Update found link
                            foundLink.get().copy(
                                price = linkDto.price ?: foundLink.get().price,
                                favourite = linkDto.favourite ?: foundLink.get().favourite
                            )
                        } else {
                            mapper.toLinkEntity(linkDto, savedToBuy)
                        }
                    }

                    val savedLink = toBuyLinkRepository.save(linkEntity)
                    updatedLinks.add(savedLink)
                }
            }

            mapper.toDto(savedToBuy.copy(links = updatedLinks))
        } else null
    }

    fun delete(id: UUID): Boolean {
        return if (toBuyRepository.existsById(id)) {
            toBuyRepository.deleteById(id)
            toBuyLinkRepository.deleteByToBuyId(id)
            true
        } else false
    }

    fun markAsBought(id: UUID): ToBuyDto? {
        return toBuyRepository.findById(id)
            .map { item ->
                val updated = toBuyRepository.save(item.copy(bought = LocalDateTime.now()))
                mapper.toDto(updated)
            }
            .orElse(null)
    }

    fun findUnboughtItems(): List<ToBuyDto> = toBuyRepository.findByBoughtIsNull().map { mapper.toDto(it) }

    fun findBoughtItems(): List<ToBuyDto> = toBuyRepository.findByBoughtIsNotNull().map { mapper.toDto(it) }

    fun findByInterest(interest: String): List<ToBuyDto> = toBuyRepository.findByInterest(interest).map { mapper.toDto(it) }

    fun findByPriceRange(minPrice: Int, maxPrice: Int): List<ToBuyDto> =
        toBuyRepository.findByEstimatedPriceBetween(minPrice, maxPrice).map { mapper.toDto(it) }

    fun searchByTitle(title: String): List<ToBuyDto> =
        toBuyRepository.findByTitleContainingIgnoreCase(title).map { mapper.toDto(it) }

    fun addLinkToItem(toBuyId: UUID, link: ToBuyLink): ToBuyLink? {
        return if (toBuyRepository.existsById(toBuyId)) {
            toBuyLinkRepository.save(link.copy(toBuyId = toBuyId))
        } else null
    }

    fun getLinksForItem(toBuyId: UUID): List<ToBuyLink> =
        toBuyLinkRepository.findByToBuyId(toBuyId)
}
