package org.ericp.ehub.server.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "TOBUY_LINKS")
data class ToBuyLink(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false, length = 255)
    val url: String,

    @Column
    val price: Short? = null,

    @Column(nullable = false)
    val favourite: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tobuy_id", nullable = false)
    @JsonIgnore
    val toBuy: ToBuy? = null,

    @Column(name = "tobuy_id", nullable = false, insertable = false, updatable = false)
    val toBuyId: UUID
)
