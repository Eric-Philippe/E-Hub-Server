package org.ericp.ehub.server.dto

import java.util.*

data class ToBuyLinkDto(
    val id: UUID? = null,
    val url: String,
    val price: Short? = null,
    val favourite: Boolean = false,
    val toBuyId: UUID? = null
)
