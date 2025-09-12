package org.ericp.ehub.server.repository

import org.ericp.ehub.server.entity.ToDo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ToDoRepository : JpaRepository<ToDo, UUID> {
    fun findByParentId(parentId: UUID): List<ToDo>
}
