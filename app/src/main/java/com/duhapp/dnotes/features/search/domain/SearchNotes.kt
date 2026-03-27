package com.duhapp.dnotes.features.search.domain

import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.note.data.NoteRepository
import javax.inject.Inject

class SearchNotes @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend fun invoke(query: String): List<BaseNoteUIModel> {
        if (query.isBlank()) return emptyList()
        return noteRepository.searchNotes(query)
    }
}
