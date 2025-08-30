package org.ericp.ehub.server.repository

import org.ericp.ehub.server.entity.ToDoCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ToDoCategoryRepository : JpaRepository<ToDoCategory, UUID> {

    fun findByNameIgnoreCase(name: String): ToDoCategory?

    fun findByColor(color: String): List<ToDoCategory>

    fun findByNameContainingIgnoreCase(name: String): List<ToDoCategory>
}
