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
    val label: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val state: State,

    @Column(length = 7)
    val color: String = "#FFFFFF",

    @Column(nullable = false)
    val created: LocalDateTime = LocalDateTime.now(),

    @Column
    val modified: LocalDateTime? = null,

    @Column
    val dueDate: LocalDateTime? = null,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @ManyToOne(optional = true)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    val parent: ToDo? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val children: MutableSet<ToDo> = mutableSetOf()
)
