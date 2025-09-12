package org.ericp.ehub.server.dto

import org.ericp.ehub.server.entity.State
import java.time.LocalDateTime
import java.util.UUID

data class ToDoDtoPartial(
    val id: UUID? = null,
    val label: String,
    val state: State,
    val color: String = "#FFFFFF",
    val created: LocalDateTime = LocalDateTime.now(),
    val modified: LocalDateTime? = null,
    val dueDate: LocalDateTime? = null,
    val description: String? = null,
    val parentId: UUID? = null
)

data class ToDoDto(
    val id: UUID? = null,
    val label: String,
    val state: State,
    val color: String = "#FFFFFF",
    val created: LocalDateTime? = LocalDateTime.now(),
    val modified: LocalDateTime? = null,
    val dueDate: LocalDateTime? = null,
    val description: String? = null,
    val parent: ToDoDto? = null,
    val children: List<ToDoDto>? = null
)