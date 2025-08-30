package org.ericp.ehub.server.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "TOBUY_CATEGORIES")
data class ToBuyCategory(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false, length = 50)
    val name: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val description: String,

    @Column(nullable = false, length = 50)
    val color: String,

    @ManyToMany(mappedBy = "tbCategories", fetch = FetchType.LAZY)
    val toBuyItems: List<ToBuy> = emptyList()
)
