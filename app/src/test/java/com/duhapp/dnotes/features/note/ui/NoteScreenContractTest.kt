package com.duhapp.dnotes.features.note.ui

import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.add_or_update_category.ui.ColorItemUIModel
import org.junit.Assert.*
import org.junit.Test

class NoteScreenContractTest {

    @Test
    fun `NoteScreenState has correct default values`() {
        val state = NoteScreenState()
        
        assertFalse(state.isLoading)
        assertNull(state.noteId)
        assertEquals("", state.title)
        assertEquals("", state.body)
        assertEquals(CategoryUIModel(), state.category)
        assertTrue(state.isEditable)
        assertFalse(state.showCategoryBottomSheet)
        assertNull(state.error)
    }

    @Test
    fun `NoteScreenState can be created with custom values`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = "",
            color = ColorItemUIModel(color = NoteColor.BLUE)
        )
        
        val state = NoteScreenState(
            isLoading = true,
            noteId = 42,
            title = "Test Title",
            body = "Test Body",
            category = category,
            showCategoryBottomSheet = true,
            error = "Error message"
        )
        
        assertTrue(state.isLoading)
        assertEquals(42, state.noteId)
        assertEquals("Test Title", state.title)
        assertEquals("Test Body", state.body)
        assertEquals("Work", state.category.name)
        assertTrue(state.showCategoryBottomSheet)
        assertEquals("Error message", state.error)
    }

    @Test
    fun `NoteScreenIntent LoadNote contains correct noteId`() {
        val intent = NoteScreenIntent.LoadNote(42)
        assertEquals(42, intent.noteId)
    }

    @Test
    fun `NoteScreenIntent LoadNote with null creates new note`() {
        val intent = NoteScreenIntent.LoadNote(null)
        assertNull(intent.noteId)
    }

    @Test
    fun `NoteScreenIntent UpdateTitle contains correct value`() {
        val intent = NoteScreenIntent.UpdateTitle("New Title")
        assertEquals("New Title", intent.title)
    }

    @Test
    fun `NoteScreenIntent UpdateBody contains correct value`() {
        val intent = NoteScreenIntent.UpdateBody("New Body")
        assertEquals("New Body", intent.body)
    }

    @Test
    fun `NoteScreenIntent SelectCategory contains correct category`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Personal",
            emoji = "🏠",
            description = ""
        )
        val intent = NoteScreenIntent.SelectCategory(category)
        assertEquals("Personal", intent.category.name)
    }

    @Test
    fun `NoteScreenIntent SaveNote is a singleton object`() {
        val intent1 = NoteScreenIntent.SaveNote
        val intent2 = NoteScreenIntent.SaveNote
        assertSame(intent1, intent2)
    }

    @Test
    fun `NoteScreenIntent ShowCategoryBottomSheet is a singleton object`() {
        val intent1 = NoteScreenIntent.ShowCategoryBottomSheet
        val intent2 = NoteScreenIntent.ShowCategoryBottomSheet
        assertSame(intent1, intent2)
    }

    @Test
    fun `NoteScreenIntent HideCategoryBottomSheet is a singleton object`() {
        val intent1 = NoteScreenIntent.HideCategoryBottomSheet
        val intent2 = NoteScreenIntent.HideCategoryBottomSheet
        assertSame(intent1, intent2)
    }

    @Test
    fun `NoteScreenIntent NavigateBack is a singleton object`() {
        val intent1 = NoteScreenIntent.NavigateBack
        val intent2 = NoteScreenIntent.NavigateBack
        assertSame(intent1, intent2)
    }

    @Test
    fun `NoteScreenEffect NavigateBack is a singleton object`() {
        val effect1 = NoteScreenEffect.NavigateBack
        val effect2 = NoteScreenEffect.NavigateBack
        assertSame(effect1, effect2)
    }

    @Test
    fun `NoteScreenEffect ShowError contains correct message`() {
        val effect = NoteScreenEffect.ShowError("Something went wrong")
        assertEquals("Something went wrong", effect.message)
    }

    @Test
    fun `NoteScreenEffect NoteSaved is a singleton object`() {
        val effect1 = NoteScreenEffect.NoteSaved
        val effect2 = NoteScreenEffect.NoteSaved
        assertSame(effect1, effect2)
    }

    @Test
    fun `getLightColorFromOrdinal returns correct colors`() {
        assertEquals(getLightColorFromOrdinal(0), androidx.compose.ui.graphics.Color(0xFFFFCDD2))
        assertEquals(getLightColorFromOrdinal(1), androidx.compose.ui.graphics.Color(0xFFC8E6C9))
        assertEquals(getLightColorFromOrdinal(2), androidx.compose.ui.graphics.Color(0xFFBBDEFB))
        assertEquals(getLightColorFromOrdinal(7), androidx.compose.ui.graphics.Color(0xFFFFE0B2))
    }

    @Test
    fun `getDarkColorFromOrdinal returns correct colors`() {
        assertEquals(getDarkColorFromOrdinal(0), androidx.compose.ui.graphics.Color(0xFFD32F2F))
        assertEquals(getDarkColorFromOrdinal(1), androidx.compose.ui.graphics.Color(0xFF388E3C))
        assertEquals(getDarkColorFromOrdinal(2), androidx.compose.ui.graphics.Color(0xFF1976D2))
        assertEquals(getDarkColorFromOrdinal(7), androidx.compose.ui.graphics.Color(0xFFF57C00))
    }
}
