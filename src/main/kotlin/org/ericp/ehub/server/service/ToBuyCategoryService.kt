package org.ericp.ehub.server.service

import org.ericp.ehub.server.dto.ToBuyCategoryDto
import org.ericp.ehub.server.entity.ToBuyCategory
import org.ericp.ehub.server.mapper.ToBuyCategoryMapper
import org.ericp.ehub.server.repository.ToBuyCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class ToBuyCategoryService(
    private val repository: ToBuyCategoryRepository,
    private val mapper: ToBuyCategoryMapper
) {

    fun findAll(): List<ToBuyCategoryDto> = repository.findAll().map { mapper.toDto(it) }

    fun findById(id: UUID): ToBuyCategoryDto? = repository.findById(id)
        .map { mapper.toDto(it) }
        .orElse(null)

    fun create(categoryDto: ToBuyCategoryDto): ToBuyCategoryDto {
        val entity = mapper.toEntity(categoryDto)
        val saved = repository.save(entity)
        return mapper.toDto(saved)
    }

    fun update(id: UUID, categoryDto: ToBuyCategoryDto): ToBuyCategoryDto? {
        return if (repository.existsById(id)) {
            val entity = mapper.toEntity(categoryDto.copy(id = id))
            val saved = repository.save(entity)
            mapper.toDto(saved)
        } else null
    }

    fun delete(id: UUID): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else false
    }

    fun findByNameIgnoreCase(name: String): ToBuyCategoryDto? =
        repository.findByNameIgnoreCase(name)?.let { mapper.toDto(it) }

    fun findByColor(color: String): List<ToBuyCategoryDto> =
        repository.findByColor(color).map { mapper.toDto(it) }

    fun findByNameContaining(name: String): List<ToBuyCategoryDto> =
        repository.findByNameContainingIgnoreCase(name).map { mapper.toDto(it) }

    /**
     * Process categories: find existing ones by ID or create new ones
     * This method is used by ToBuyService to handle category associations
     */
    fun processCategories(categoryDtos: List<ToBuyCategoryDto>): List<ToBuyCategory> {
        return categoryDtos.map { dto ->
            if (dto.id != null) {
                // If category has an ID, try to find existing one first
                repository.findById(dto.id).orElse(null) ?: run {
                    // If not found by ID and we have name, try to find by name
                    if (dto.name != null) {
                        repository.findByNameIgnoreCase(dto.name) ?: run {
                            // Create new category with provided data
                            val entity = mapper.toEntity(dto.copy(id = null))
                            repository.save(entity)
                        }
                    } else {
                        // ID provided but category doesn't exist and no name provided
                        throw IllegalArgumentException("Category with ID ${dto.id} not found")
                    }
                }
            } else {
                // If no ID provided, we need name to check for existing or create new
                if (dto.name != null) {
                    repository.findByNameIgnoreCase(dto.name) ?: run {
                        // Create new category if none exists with this name
                        val entity = mapper.toEntity(dto)
                        repository.save(entity)
                    }
                } else {
                    throw IllegalArgumentException("Either ID or name must be provided for categories")
                }
            }
        }
    }
}
