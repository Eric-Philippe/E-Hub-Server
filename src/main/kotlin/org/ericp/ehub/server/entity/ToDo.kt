package org.ericp.ehub.server.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

enum class State {
    TODO,
    IN_PROGRESS,
    DONE
}

@Entity
@Table(name = "TODO")
data class ToDo(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false, length = 50)
    var label: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var state: State,

    @Column(length = 7)
    var color: String? = null,

    @Column(nullable = false)
    val created: LocalDateTime = LocalDateTime.now(),

    var modified: LocalDateTime? = null,
    var dueDate: LocalDateTime? = null,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: ToDo? = null
)
