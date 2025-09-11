package org.ericp.ehub.server.mapper

import org.ericp.ehub.server.dto.NoteDto
import org.ericp.ehub.server.entity.Note
import org.ericp.ehub.server.utils.Utils
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class NoteMapper {
    fun toDto(entity: Note, key: String): NoteDto {
        return NoteDto(
            id = entity.id,
            content = Utils.decodeWithKey(entity.content, key),
            key = key,
            created = entity.created,
            modified = entity.modified
        )
    }

    fun toEntity(dto: NoteDto): Note {
        return Note(
            id = dto.id, // Let Hibernate manage ID generation for new entities
            content = Utils.encodeWithKey(dto.content, dto.key),
            created = dto.created ?: LocalDateTime.now(),
            modified = dto.modified
        )
    }
}