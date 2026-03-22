package com.duhapp.dnotes.features.home.ui

import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.HomeCategoryUIModel
import org.junit.Assert.*
import org.junit.Test

class HomeScreenContractTest {

    @Test
    fun `HomeScreenState has correct default values`() {
        val state = HomeScreenState()
        
        assertFalse(state.isLoading)
        assertTrue(state.categories.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `HomeScreenState can be created with custom values`() {
        val categories = listOf(
            HomeCategoryUIModel(
                id = 1,
                title = "Work",
                noteList = emptyList()
            ),
            HomeCategoryUIModel(
                id = 2,
                title = "Personal",
                noteList = emptyList()
            )
        )
        
        val state = HomeScreenState(
            isLoading = true,
            categories = categories,
            error = "Error message"
        )
        
        assertTrue(state.isLoading)
        assertEquals(2, state.categories.size)
        assertEquals("Work", state.categories[0].title)
        assertEquals("Personal", state.categories[1].title)
        assertEquals("Error message", state.error)
    }

    @Test
    fun `HomeScreenIntent LoadCategories is a singleton object`() {
        val intent1 = HomeScreenIntent.LoadCategories
        val intent2 = HomeScreenIntent.LoadCategories
        assertSame(intent1, intent2)
    }

    @Test
    fun `HomeScreenIntent NoteClicked contains correct noteId`() {
        val intent = HomeScreenIntent.NoteClicked(42)
        assertEquals(42, intent.noteId)
    }

    @Test
    fun `HomeScreenIntent ViewAllClicked contains correct categoryId`() {
        val intent = HomeScreenIntent.ViewAllClicked(42)
        assertEquals(42, intent.categoryId)
    }

    @Test
    fun `HomeScreenEffect NavigateToNote contains correct noteId`() {
        val effect = HomeScreenEffect.NavigateToNote(42)
        assertEquals(42, effect.noteId)
    }

    @Test
    fun `HomeScreenEffect NavigateToAllNotes contains correct categoryId`() {
        val effect = HomeScreenEffect.NavigateToAllNotes(42)
        assertEquals(42, effect.categoryId)
    }

    @Test
    fun `HomeScreenEffect ShowError contains correct message`() {
        val effect = HomeScreenEffect.ShowError("Something went wrong")
        assertEquals("Something went wrong", effect.message)
    }
}
