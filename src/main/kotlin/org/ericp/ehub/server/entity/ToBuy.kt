package org.ericp.ehub.server.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "TOBUY")
data class ToBuy(
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 50)
    val title: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(columnDefinition = "TEXT")
    val criteria: String? = null,

    @Column
    val bought: LocalDateTime? = null,

    @Column(name = "estimated_price")
    val estimatedPrice: Int? = null,

    @Column(length = 50)
    val interest: String? = null
)
