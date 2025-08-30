package org.ericp.ehub.server.service

import org.ericp.ehub.server.entity.BannerMessage
import org.ericp.ehub.server.repository.BannerMessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class BannerMessageService(
    private val bannerMessageRepository: BannerMessageRepository
) {

    fun findAll(): List<BannerMessage> = bannerMessageRepository.findAll()

    fun findById(id: UUID): BannerMessage? = bannerMessageRepository.findById(id).orElse(null)

    fun create(message: BannerMessage): BannerMessage = bannerMessageRepository.save(message)

    fun update(id: UUID, updatedMessage: BannerMessage): BannerMessage? {
        return if (bannerMessageRepository.existsById(id)) {
            bannerMessageRepository.save(updatedMessage.copy(id = id))
        } else null
    }

    fun delete(id: UUID): Boolean {
        return if (bannerMessageRepository.existsById(id)) {
            bannerMessageRepository.deleteById(id)
            true
        } else false
    }

    fun findAllOrderedByPriority(): List<BannerMessage> =
        bannerMessageRepository.findByOrderByPriorityAsc()

    fun findByMaxPriority(maxPriority: Short): List<BannerMessage> =
        bannerMessageRepository.findByPriorityLessThanEqual(maxPriority)

    fun findHighestPriorityMessages(): List<BannerMessage> =
        bannerMessageRepository.findHighestPriorityMessages()
}
