package org.ericp.ehub.server.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "BANNER_MESSAGES")
data class BannerMessage(
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 150)
    val message: String,

    @Column(nullable = false)
    val priority: Short
)
