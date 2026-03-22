package com.duhapp.dnotes.features.all_notes.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.add_or_update_category.ui.ColorItemUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BasicNoteUIModel
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class AllNotesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `AllNotesScreen displays loading state`() {
        composeTestRule.setContent {
            AllNotesScreen(
                state = AllNotesScreenState(isLoading = true),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Loading...").assertExists()
    }

    @Test
    fun `AllNotesScreen displays error state`() {
        val errorMessage = "Failed to load notes"
        composeTestRule.setContent {
            AllNotesScreen(
                state = AllNotesScreenState(error = errorMessage),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText(errorMessage).assertExists()
    }

    @Test
    fun `AllNotesScreen displays category name in header`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = "",
            color = ColorItemUIModel(color = NoteColor.BLUE)
        )

        composeTestRule.setContent {
            AllNotesScreen(
                state = AllNotesScreenState(
                    category = category,
                    notes = emptyList()
                ),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Work").assertExists()
    }

    @Test
    fun `AllNotesScreen displays category emoji in header`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Personal",
            emoji = "🏠",
            description = "",
            color = ColorItemUIModel(color = NoteColor.GREEN)
        )

        composeTestRule.setContent {
            AllNotesScreen(
                state = AllNotesScreenState(
                    category = category,
                    notes = emptyList()
                ),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("🏠").assertExists()
    }

    @Test
    fun `AllNotesScreen displays empty state message when no notes`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = "",
            color = ColorItemUIModel(color = NoteColor.BLUE)
        )

        composeTestRule.setContent {
            AllNotesScreen(
                state = AllNotesScreenState(
                    category = category,
                    notes = emptyList()
                ),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("No notes found").assertExists()
    }

    @Test
    fun `AllNotesScreen displays notes`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = "",
            color = ColorItemUIModel(color = NoteColor.BLUE)
        )
        val notes = listOf(
            BasicNoteUIModel(
                id = 1,
                isPinned = false,
                isCompleted = false,
                isCompletable = false,
                category = category,
                title = "Meeting Notes",
                body = "Discuss project timeline",
                color = 0
            ),
            BasicNoteUIModel(
                id = 2,
                isPinned = false,
                isCompleted = false,
                isCompletable = false,
                category = category,
                title = "Shopping List",
                body = "Buy groceries",
                color = 1
            )
        )

        composeTestRule.setContent {
            AllNotesScreen(
                state = AllNotesScreenState(
                    category = category,
                    notes = notes
                ),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Meeting Notes").assertExists()
        composeTestRule.onNodeWithText("Shopping List").assertExists()
    }

    @Test
    fun `AllNotesScreen triggers NoteClicked intent when note clicked`() {
        var intent: AllNotesScreenIntent? = null
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
            body = "Test body",
            color = 0
        )

        composeTestRule.setContent {
            AllNotesScreen(
                state = AllNotesScreenState(
                    category = category,
                    notes = listOf(note)
                ),
                onIntent = { intent = it }
            )
        }

        composeTestRule.onNodeWithText("Test Note").performClick()

        assertTrue(intent is AllNotesScreenIntent.NoteClicked)
        assertEquals("Test Note", (intent as AllNotesScreenIntent.NoteClicked).note.title)
    }

    @Test
    fun `AllNotesNoteItem displays note title`() {
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = CategoryUIModel(
                id = 1,
                name = "Work",
                emoji = "💼",
                description = ""
            ),
            title = "Important Task",
            body = "Complete by Friday",
            color = 0
        )

        composeTestRule.setContent {
            AllNotesNoteItem(
                note = note,
                isSelectable = false,
                isSelected = false,
                onClick = {},
                onLongClick = {},
                onEdit = {},
                onDelete = {},
                onMove = {}
            )
        }

        composeTestRule.onNodeWithText("Important Task").assertExists()
    }

    @Test
    fun `AllNotesNoteItem displays note body`() {
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = CategoryUIModel(
                id = 1,
                name = "Work",
                emoji = "💼",
                description = ""
            ),
            title = "Important Task",
            body = "Complete by Friday",
            color = 0
        )

        composeTestRule.setContent {
            AllNotesNoteItem(
                note = note,
                isSelectable = false,
                isSelected = false,
                onClick = {},
                onLongClick = {},
                onEdit = {},
                onDelete = {},
                onMove = {}
            )
        }

        composeTestRule.onNodeWithText("Complete by Friday").assertExists()
    }

    @Test
    fun `AllNotesNoteItem triggers onClick when tapped`() {
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = CategoryUIModel(
                id = 1,
                name = "Work",
                emoji = "💼",
                description = ""
            ),
            title = "Test Note",
            body = "Test body",
            color = 0
        )

        var clicked = false

        composeTestRule.setContent {
            AllNotesNoteItem(
                note = note,
                isSelectable = false,
                isSelected = false,
                onClick = { clicked = true },
                onLongClick = {},
                onEdit = {},
                onDelete = {},
                onMove = {}
            )
        }

        composeTestRule.onNodeWithText("Test Note").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `AllNotesNoteItem shows selected indicator when selected`() {
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = CategoryUIModel(
                id = 1,
                name = "Work",
                emoji = "💼",
                description = ""
            ),
            title = "Test Note",
            body = "Test body",
            color = 0
        )

        composeTestRule.setContent {
            AllNotesNoteItem(
                note = note,
                isSelectable = true,
                isSelected = true,
                onClick = {},
                onLongClick = {},
                onEdit = {},
                onDelete = {},
                onMove = {}
            )
        }

        composeTestRule.onNodeWithText("✓").assertExists()
    }

    @Test
    fun `getNoteColor returns correct colors for ordinals`() {
        assertEquals(getNoteColor(0), androidx.compose.ui.graphics.Color(0xFFFFCDD2))
        assertEquals(getNoteColor(1), androidx.compose.ui.graphics.Color(0xFFC8E6C9))
        assertEquals(getNoteColor(2), androidx.compose.ui.graphics.Color(0xFFBBDEFB))
        assertEquals(getNoteColor(7), androidx.compose.ui.graphics.Color(0xFFFFE0B2))
        assertEquals(getNoteColor(99), androidx.compose.ui.graphics.Color(0xFFFFFFFF))
    }

    @Test
    fun `getCategoryColor returns correct colors for ordinals`() {
        assertEquals(getCategoryColor(0), androidx.compose.ui.graphics.Color(0xFFD32F2F))
        assertEquals(getCategoryColor(1), androidx.compose.ui.graphics.Color(0xFF388E3C))
        assertEquals(getCategoryColor(2), androidx.compose.ui.graphics.Color(0xFF1976D2))
        assertEquals(getCategoryColor(7), androidx.compose.ui.graphics.Color(0xFFF57C00))
        assertEquals(getCategoryColor(99), androidx.compose.ui.graphics.Color(0xFF4CAF50))
    }
}
