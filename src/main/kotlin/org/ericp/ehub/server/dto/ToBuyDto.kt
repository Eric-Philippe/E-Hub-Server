package org.ericp.ehub.server.dto

import java.time.LocalDateTime
import java.util.*

data class ToBuyDto(
    val id: UUID? = null,
    val title: String,
    val description: String? = null,
    val criteria: String? = null,
    val bought: LocalDateTime? = null,
    val estimatedPrice: Int? = null,
    val interest: String? = null,
    var categories: List<ToBuyCategoryDto> = emptyList(),
    val links: List<ToBuyLinkDto> = emptyList()
)
