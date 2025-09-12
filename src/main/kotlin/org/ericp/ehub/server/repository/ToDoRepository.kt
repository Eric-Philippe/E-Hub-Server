package org.ericp.ehub.server.repository

import org.ericp.ehub.server.entity.ToDo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ToDoRepository : JpaRepository<ToDo, UUID> {
    // Fetch a ToDo by ID with its parent and children eagerly loaded
    @Query("""
        SELECT t FROM ToDo t
        LEFT JOIN FETCH t.parent
        LEFT JOIN FETCH t.children
        WHERE t.id = :id
    """)
    fun findByIdWithTree(id: UUID): ToDo?

    // Fetch all ToDos with their children eagerly loaded
    @Query("SELECT DISTINCT t FROM ToDo t LEFT JOIN FETCH t.children")
    fun findAllWithChildren(): List<ToDo>

    // Fetch all root ToDos with their children eagerly loaded
    @Query("SELECT DISTINCT t FROM ToDo t LEFT JOIN FETCH t.children WHERE t.parent IS NULL")
    fun findRootsWithChildren(): List<ToDo>

    // Fetch all children recursively (flat list)
    @Query("""
        SELECT t FROM ToDo t
        WHERE t.parent.id = :parentId
    """)
    fun findAllChildren(parentId: UUID): List<ToDo>

    // Fetch all ToDos with a given parent (direct children only)
    fun findByParentId(parentId: UUID): List<ToDo>

    // Fetch all ToDos with no parent (root nodes)
    fun findByParentIsNull(): List<ToDo>
}
