package org.ericp.ehub.server.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "NONOGRAM_LOGS")
data class NonogramLog(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false)
    val started: LocalDateTime,

    @Column(nullable = true)
    val ended: LocalDateTime?
)