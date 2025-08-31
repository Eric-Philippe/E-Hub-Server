package org.ericp.ehub.server.dto

import java.util.UUID

data class ToDoCategoryDto(
    val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    var color: String? = null
)