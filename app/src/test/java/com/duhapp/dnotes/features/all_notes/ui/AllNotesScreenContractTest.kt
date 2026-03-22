package com.duhapp.dnotes.features.all_notes.ui

import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.add_or_update_category.ui.ColorItemUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BasicNoteUIModel
import org.junit.Assert.*
import org.junit.Test

class AllNotesScreenContractTest {

    @Test
    fun `AllNotesScreenState has correct default values`() {
        val state = AllNotesScreenState()
        
        assertFalse(state.isLoading)
        assertEquals(CategoryUIModel(), state.category)
        assertTrue(state.notes.isEmpty())
        assertFalse(state.isSelectable)
        assertTrue(state.selectedNoteIds.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `AllNotesScreenState can be created with custom values`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = "",
            color = ColorItemUIModel(color = NoteColor.BLUE)
        )
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = category,
            title = "Test Note",
            body = "Test Body",
            color = 0
        )
        
        val state = AllNotesScreenState(
            isLoading = true,
            category = category,
            notes = listOf(note),
            isSelectable = true,
            selectedNoteIds = setOf(1),
            error = "Error message"
        )
        
        assertTrue(state.isLoading)
        assertEquals("Work", state.category.name)
        assertEquals(1, state.notes.size)
        assertTrue(state.isSelectable)
        assertEquals(setOf(1), state.selectedNoteIds)
        assertEquals("Error message", state.error)
    }

    @Test
    fun `AllNotesScreenIntent LoadNotes contains correct categoryId`() {
        val intent = AllNotesScreenIntent.LoadNotes(42)
        assertEquals(42, intent.categoryId)
    }

    @Test
    fun `AllNotesScreenIntent NoteClicked contains correct note`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = category,
            title = "Test",
            body = "Body",
            color = 0
        )
        
        val intent = AllNotesScreenIntent.NoteClicked(note)
        assertEquals("Test", intent.note.title)
    }

    @Test
    fun `AllNotesScreenIntent NoteLongClicked contains correct note`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = category,
            title = "Test",
            body = "Body",
            color = 0
        )
        
        val intent = AllNotesScreenIntent.NoteLongClicked(note)
        assertEquals("Test", intent.note.title)
    }

    @Test
    fun `AllNotesScreenIntent ToggleNoteSelection contains correct note`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = category,
            title = "Test",
            body = "Body",
            color = 0
        )
        
        val intent = AllNotesScreenIntent.ToggleNoteSelection(note)
        assertEquals("Test", intent.note.title)
    }

    @Test
    fun `AllNotesScreenIntent DeleteNote contains correct note`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = category,
            title = "Test",
            body = "Body",
            color = 0
        )
        
        val intent = AllNotesScreenIntent.DeleteNote(note)
        assertEquals("Test", intent.note.title)
    }

    @Test
    fun `AllNotesScreenIntent MoveNote contains correct note`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = category,
            title = "Test",
            body = "Body",
            color = 0
        )
        
        val intent = AllNotesScreenIntent.MoveNote(note)
        assertEquals("Test", intent.note.title)
    }

    @Test
    fun `AllNotesScreenIntent DeleteSelectedNotes is a singleton object`() {
        val intent1 = AllNotesScreenIntent.DeleteSelectedNotes
        val intent2 = AllNotesScreenIntent.DeleteSelectedNotes
        assertSame(intent1, intent2)
    }

    @Test
    fun `AllNotesScreenIntent NavigateBack is a singleton object`() {
        val intent1 = AllNotesScreenIntent.NavigateBack
        val intent2 = AllNotesScreenIntent.NavigateBack
        assertSame(intent1, intent2)
    }

    @Test
    fun `AllNotesScreenEffect NavigateToNote contains correct note`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = category,
            title = "Test",
            body = "Body",
            color = 0
        )
        
        val effect = AllNotesScreenEffect.NavigateToNote(note)
        assertEquals("Test", effect.note.title)
    }

    @Test
    fun `AllNotesScreenEffect NavigateBack is a singleton object`() {
        val effect1 = AllNotesScreenEffect.NavigateBack
        val effect2 = AllNotesScreenEffect.NavigateBack
        assertSame(effect1, effect2)
    }

    @Test
    fun `AllNotesScreenEffect ShowError contains correct message`() {
        val effect = AllNotesScreenEffect.ShowError("Something went wrong")
        assertEquals("Something went wrong", effect.message)
    }

    @Test
    fun `AllNotesScreenEffect ShowMoveDialog contains correct note`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = category,
            title = "Test",
            body = "Body",
            color = 0
        )
        
        val effect = AllNotesScreenEffect.ShowMoveDialog(note)
        assertEquals("Test", effect.note.title)
    }

    @Test
    fun `AllNotesScreenEffect NotesDeleted is a singleton object`() {
        val effect1 = AllNotesScreenEffect.NotesDeleted
        val effect2 = AllNotesScreenEffect.NotesDeleted
        assertSame(effect1, effect2)
    }

    @Test
    fun `getNoteColor returns correct colors`() {
        assertEquals(getNoteColor(0), androidx.compose.ui.graphics.Color(0xFFFFCDD2))
        assertEquals(getNoteColor(1), androidx.compose.ui.graphics.Color(0xFFC8E6C9))
        assertEquals(getNoteColor(2), androidx.compose.ui.graphics.Color(0xFFBBDEFB))
        assertEquals(getNoteColor(3), androidx.compose.ui.graphics.Color(0xFFFFF9C4))
        assertEquals(getNoteColor(4), androidx.compose.ui.graphics.Color(0xFFE1BEE7))
        assertEquals(getNoteColor(5), androidx.compose.ui.graphics.Color(0xFFB2EBF2))
        assertEquals(getNoteColor(6), androidx.compose.ui.graphics.Color(0xFFD7CCC8))
        assertEquals(getNoteColor(7), androidx.compose.ui.graphics.Color(0xFFFFE0B2))
        assertEquals(getNoteColor(99), androidx.compose.ui.graphics.Color(0xFFFFFFFF))
    }

    @Test
    fun `getCategoryColor returns correct colors`() {
        assertEquals(getCategoryColor(0), androidx.compose.ui.graphics.Color(0xFFD32F2F))
        assertEquals(getCategoryColor(1), androidx.compose.ui.graphics.Color(0xFF388E3C))
        assertEquals(getCategoryColor(2), androidx.compose.ui.graphics.Color(0xFF1976D2))
        assertEquals(getCategoryColor(3), androidx.compose.ui.graphics.Color(0xFFFBC02D))
        assertEquals(getCategoryColor(4), androidx.compose.ui.graphics.Color(0xFF7B1FA2))
        assertEquals(getCategoryColor(5), androidx.compose.ui.graphics.Color(0xFF0097A7))
        assertEquals(getCategoryColor(6), androidx.compose.ui.graphics.Color(0xFF5D4037))
        assertEquals(getCategoryColor(7), androidx.compose.ui.graphics.Color(0xFFF57C00))
        assertEquals(getCategoryColor(99), androidx.compose.ui.graphics.Color(0xFF4CAF50))
    }
}
