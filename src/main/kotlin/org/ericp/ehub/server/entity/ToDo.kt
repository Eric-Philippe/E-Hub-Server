package org.ericp.ehub.server.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "TODO")
data class ToDo(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false, length = 50)
    val title: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null
)
