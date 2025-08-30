package org.ericp.ehub.server.service

import org.ericp.ehub.server.entity.ToBuyCategory
import org.ericp.ehub.server.entity.ToDoCategory
import org.ericp.ehub.server.repository.ToBuyCategoryRepository
import org.ericp.ehub.server.repository.ToDoCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class CategoryService(
    private val toBuyCategoryRepository: ToBuyCategoryRepository,
    private val toDoCategoryRepository: ToDoCategoryRepository
) {

    // ToBuy Categories
    fun findAllToBuyCategories(): List<ToBuyCategory> = toBuyCategoryRepository.findAll()

    fun findToBuyCategoryById(id: UUID): ToBuyCategory? = toBuyCategoryRepository.findById(id).orElse(null)

    fun createToBuyCategory(category: ToBuyCategory): ToBuyCategory = toBuyCategoryRepository.save(category)

    fun updateToBuyCategory(id: UUID, updatedCategory: ToBuyCategory): ToBuyCategory? {
        return if (toBuyCategoryRepository.existsById(id)) {
            toBuyCategoryRepository.save(updatedCategory.copy(id = id))
        } else null
    }

    fun deleteToBuyCategory(id: UUID): Boolean {
        return if (toBuyCategoryRepository.existsById(id)) {
            toBuyCategoryRepository.deleteById(id)
            true
        } else false
    }

    fun findToBuyCategoryByName(name: String): ToBuyCategory? =
        toBuyCategoryRepository.findByNameIgnoreCase(name)

    // ToDo Categories
    fun findAllToDoCategories(): List<ToDoCategory> = toDoCategoryRepository.findAll()

    fun findToDoCategoryById(id: UUID): ToDoCategory? = toDoCategoryRepository.findById(id).orElse(null)

    fun createToDoCategory(category: ToDoCategory): ToDoCategory = toDoCategoryRepository.save(category)

    fun updateToDoCategory(id: UUID, updatedCategory: ToDoCategory): ToDoCategory? {
        return if (toDoCategoryRepository.existsById(id)) {
            toDoCategoryRepository.save(updatedCategory.copy(id = id))
        } else null
    }

    fun deleteToDoCategory(id: UUID): Boolean {
        return if (toDoCategoryRepository.existsById(id)) {
            toDoCategoryRepository.deleteById(id)
            true
        } else false
    }

    fun findToDoCategoryByName(name: String): ToDoCategory? =
        toDoCategoryRepository.findByNameIgnoreCase(name)
}
