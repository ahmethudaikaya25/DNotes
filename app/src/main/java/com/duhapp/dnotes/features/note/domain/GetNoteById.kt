package com.duhapp.dnotes.features.note.domain

import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.note.data.NoteRepository

class GetNoteById(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: Int): BaseNoteUIModel? {
        return noteRepository.getNoteById(noteId)
    }
}
