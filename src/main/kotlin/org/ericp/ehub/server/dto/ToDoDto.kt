package org.ericp.ehub.server.dto

import java.util.UUID

data class ToDoDto(
    val id: UUID? = null,
    val title: String,
    val description: String? = null,
    val categories: List<ToDoCategoryDto>? = emptyList(),
    val subToDos: List<ToDoDto>? = emptyList()
)