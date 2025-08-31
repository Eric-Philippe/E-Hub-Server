package org.ericp.ehub.server.service

import jakarta.transaction.Transactional
import org.ericp.ehub.server.dto.ToDoCategoryDto
import org.ericp.ehub.server.entity.ToDoCategory
import org.ericp.ehub.server.mapper.ToDoCategoryMapper
import org.ericp.ehub.server.repository.ToDoCategoryRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class ToDoCategoryService(
    private val repository: ToDoCategoryRepository,
    private val mapper: ToDoCategoryMapper
) {
    fun findAll(): List<ToDoCategoryDto> = repository.findAll().map { mapper.toDto(it) }

    fun findById(id: UUID): ToDoCategoryDto? = repository.findById(id)
        .map { mapper.toDto(it) }
        .orElse(null)

    fun create(categoryDto: ToDoCategoryDto): ToDoCategoryDto {
        val entity = mapper.toEntity(categoryDto)
        val saved = repository.save(entity)
        return mapper.toDto(saved)
    }

        fun update(id: UUID, categoryDto: ToDoCategoryDto): ToDoCategoryDto? {
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

    fun findByNameIgnoreCase(name: String): ToDoCategoryDto? =
        repository.findByNameIgnoreCase(name)?.let { mapper.toDto(it) }

    fun findByColor(color: String): List<ToDoCategoryDto> =
        repository.findByColor(color).map { mapper.toDto(it) }

    fun findByNameContaining(name: String): List<ToDoCategoryDto> =
        repository.findByNameContainingIgnoreCase(name).map { mapper.toDto(it) }

    /**
     * Process categories: find existing ones by ID or create new ones
     * This method is used by ToDoService to handle category associations
     */
    fun processCategories(categoryDtos: List<ToDoCategoryDto>): List<ToDoCategory> {
        return categoryDtos.map { dto ->
            if (dto.id != null) {
                // If category has an ID , try to find existing one first
                repository.findById(dto.id).orElse(null) ?: run {
                    // If not found by ID and we have name, try to find by name
                    if (dto.name != null) {
                        repository.findByNameIgnoreCase(dto.name) ?: run {
                            val entity = mapper.toEntity(dto.copy(id = null))
                            repository.save(entity)
                        }
                    } else {
                        // No ID and no name, cannot process
                        throw IllegalArgumentException("Category must have either an ID or a name")
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