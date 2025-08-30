package org.ericp.ehub.server.dto

import java.util.UUID

data class ToBuyCategoryDto(
    val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val color: String? = null
)