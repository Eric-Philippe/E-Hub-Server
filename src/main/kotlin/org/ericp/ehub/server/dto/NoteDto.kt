package org.ericp.ehub.server.dto

import java.time.LocalDateTime
import java.util.UUID

data class NoteDto(
    val id: UUID? = null,
    val content: String,
    val key: String,
    val created: LocalDateTime? = null,
    var modified: LocalDateTime? = null
)

data class NoteFindDto(
    val id: UUID,
    val key: String
)