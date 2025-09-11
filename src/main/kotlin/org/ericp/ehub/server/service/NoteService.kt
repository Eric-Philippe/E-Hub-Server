package org.ericp.ehub.server.service

import org.ericp.ehub.server.dto.NoteDto
import org.ericp.ehub.server.dto.NoteFindDto
import org.ericp.ehub.server.mapper.NoteMapper
import org.ericp.ehub.server.repository.NoteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class NoteService(
    private val noteRepository: NoteRepository,
    private val mapper: NoteMapper
) {
    fun findAll(key: String) = noteRepository.findAll().map { mapper.toDto(it, key) }

    fun findById(noteFindDto: NoteFindDto): NoteDto? = noteRepository.findById(noteFindDto.id)
        .map { mapper.toDto(it, noteFindDto.key) }
        .orElse(null)

    fun create(noteDto: NoteDto): NoteDto {
        val noteEntity = mapper.toEntity(noteDto)
        val savedNote = noteRepository.save(noteEntity)
        return mapper.toDto(savedNote, noteDto.key)
    }

    @Transactional
    fun update(updatedNoteDto: NoteDto, id: UUID): NoteDto? {
        val currentNote = noteRepository.findById(id)

        return if (!currentNote.isEmpty) {
            val noteEntity = mapper.toEntity(updatedNoteDto.copy(id = id))
            val savedNote = noteRepository.save(noteEntity)
            mapper.toDto(savedNote, updatedNoteDto.key)
        } else null
    }

    fun delete(id: UUID): Boolean {
        return if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id)
            true
        } else false
    }
}