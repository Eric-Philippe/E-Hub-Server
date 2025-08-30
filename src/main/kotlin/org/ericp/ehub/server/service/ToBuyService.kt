package org.ericp.ehub.server.service

import org.ericp.ehub.server.dto.ToBuyDto
import org.ericp.ehub.server.entity.ToBuyLink
import org.ericp.ehub.server.mapper.ToBuyMapper
import org.ericp.ehub.server.repository.ToBuyRepository
import org.ericp.ehub.server.repository.ToBuyLinkRepository
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
    private val mapper: ToBuyMapper
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
    fun update(id: UUID, updatedToBuyDto: ToBuyDto): ToBuyDto? {
        return if (toBuyRepository.existsById(id)) {
            // Process categories: find existing ones by ID or create new ones
            val categories = toBuyCategoryService.processCategories(updatedToBuyDto.categories)

            val toBuyEntity = mapper.toEntity(updatedToBuyDto.copy(id = id), categories)
            val savedToBuy = toBuyRepository.save(toBuyEntity)

            // Delete existing links and save new ones
            toBuyLinkRepository.deleteByToBuyId(id)
            val savedLinks = updatedToBuyDto.links.map { linkDto ->
                val linkEntity = mapper.toLinkEntity(linkDto, savedToBuy)
                toBuyLinkRepository.save(linkEntity)
            }

            mapper.toDto(savedToBuy.copy(links = savedLinks))
        } else null
    }

    fun delete(id: UUID): Boolean {
        return if (toBuyRepository.existsById(id)) {
            toBuyRepository.deleteById(id)
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
