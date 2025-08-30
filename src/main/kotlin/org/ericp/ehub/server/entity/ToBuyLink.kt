package org.ericp.ehub.server.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "TOBUY_LINKS")
data class ToBuyLink(
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 255)
    val url: String,

    @Column
    val price: Short? = null,

    @Column(nullable = false)
    val favourite: Boolean = false,

    @Column(name = "tobuy_id", nullable = false)
    val toBuyId: UUID
)
