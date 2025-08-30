package org.ericp.ehub.server.service

import org.ericp.ehub.server.entity.ToBuy
import org.ericp.ehub.server.entity.ToBuyLink
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
    private val toBuyLinkRepository: ToBuyLinkRepository
) {

    fun findAll(): List<ToBuy> = toBuyRepository.findAll()

    fun findById(id: UUID): ToBuy? = toBuyRepository.findById(id).orElse(null)

    fun create(toBuy: ToBuy): ToBuy = toBuyRepository.save(toBuy)

    fun update(id: UUID, updatedToBuy: ToBuy): ToBuy? {
        return if (toBuyRepository.existsById(id)) {
            toBuyRepository.save(updatedToBuy.copy(id = id))
        } else null
    }

    fun delete(id: UUID): Boolean {
        return if (toBuyRepository.existsById(id)) {
            toBuyRepository.deleteById(id)
            true
        } else false
    }

    fun markAsBought(id: UUID): ToBuy? {
        return findById(id)?.let { item ->
            toBuyRepository.save(item.copy(bought = LocalDateTime.now()))
        }
    }

    fun findUnboughtItems(): List<ToBuy> = toBuyRepository.findByBoughtIsNull()

    fun findBoughtItems(): List<ToBuy> = toBuyRepository.findByBoughtIsNotNull()

    fun findByInterest(interest: String): List<ToBuy> = toBuyRepository.findByInterest(interest)

    fun findByPriceRange(minPrice: Int, maxPrice: Int): List<ToBuy> =
        toBuyRepository.findByEstimatedPriceBetween(minPrice, maxPrice)

    fun searchByTitle(title: String): List<ToBuy> =
        toBuyRepository.findByTitleContainingIgnoreCase(title)

    fun addLinkToItem(toBuyId: UUID, link: ToBuyLink): ToBuyLink? {
        return if (toBuyRepository.existsById(toBuyId)) {
            toBuyLinkRepository.save(link.copy(toBuyId = toBuyId))
        } else null
    }

    fun getLinksForItem(toBuyId: UUID): List<ToBuyLink> =
        toBuyLinkRepository.findByToBuyId(toBuyId)
}
