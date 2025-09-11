package org.ericp.ehub.server.controller

import org.ericp.ehub.server.dto.NoteDto
import org.ericp.ehub.server.dto.NoteFindDto
import org.ericp.ehub.server.service.NoteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/notes")
@CrossOrigin
class NoteController(
    private val noteService: NoteService
) {

    @PostMapping("/all")
    fun getAllNotes(@RequestBody key: String): List<NoteDto> = noteService.findAll(key)

    @PostMapping("/get")
    fun getNoteById(@RequestBody noteFindDto: NoteFindDto): ResponseEntity<NoteDto> {
        return noteService.findById(noteFindDto)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/create")
    fun createNote(@RequestBody note: NoteDto): ResponseEntity<NoteDto> {
        val created = noteService.create(note)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/update/{id}")
    fun updateNote(@PathVariable id: UUID, @RequestBody updatedNote: NoteDto): ResponseEntity<NoteDto> {
        updatedNote.modified = LocalDateTime.now()
        return noteService.update(updatedNote, id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/delete/{id}")
    fun deleteNote(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (noteService.delete(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

