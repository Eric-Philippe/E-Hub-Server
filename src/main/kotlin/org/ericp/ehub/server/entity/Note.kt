package org.ericp.ehub.server.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "NOTES")
data class Note(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String,

    @Column(nullable = false)
    val created: LocalDateTime = LocalDateTime.now(),

    @Column
    val modified: LocalDateTime? = null
)