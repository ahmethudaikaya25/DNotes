package com.duhapp.dnotes.features.note.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.add_or_update_category.ui.ColorItemUIModel
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class NoteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `NoteScreen displays loading state`() {
        composeTestRule.setContent {
            NoteScreen(
                state = NoteScreenState(isLoading = true),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Loading...").assertExists()
    }

    @Test
    fun `NoteScreen displays error state`() {
        val errorMessage = "Failed to load note"
        composeTestRule.setContent {
            NoteScreen(
                state = NoteScreenState(error = errorMessage),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText(errorMessage).assertExists()
    }

    @Test
    fun `NoteScreen displays title placeholder when empty`() {
        composeTestRule.setContent {
            NoteScreen(
                state = NoteScreenState(
                    title = "",
                    body = "Some body content"
                ),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Title").assertExists()
    }

    @Test
    fun `NoteScreen displays body placeholder when empty`() {
        composeTestRule.setContent {
            NoteScreen(
                state = NoteScreenState(
                    title = "Some title",
                    body = ""
                ),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Details").assertExists()
    }

    @Test
    fun `NoteScreen displays note title`() {
        composeTestRule.setContent {
            NoteScreen(
                state = NoteScreenState(
                    title = "My Note Title",
                    body = "Note body"
                ),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("My Note Title").assertExists()
    }

    @Test
    fun `NoteScreen displays note body`() {
        composeTestRule.setContent {
            NoteScreen(
                state = NoteScreenState(
                    title = "My Note",
                    body = "This is the note body content"
                ),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("This is the note body content").assertExists()
    }

    @Test
    fun `NoteScreen displays category with emoji and name`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = "",
            color = ColorItemUIModel(color = NoteColor.BLUE)
        )

        composeTestRule.setContent {
            NoteScreen(
                state = NoteScreenState(
                    title = "Note",
                    body = "Body",
                    category = category
                ),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("💼 Work").assertExists()
    }

    @Test
    fun `NoteScreen triggers NavigateBack intent when back button clicked`() {
        var intent: NoteScreenIntent? = null

        composeTestRule.setContent {
            NoteScreen(
                state = NoteScreenState(title = "Note", body = "Body"),
                onIntent = { intent = it }
            )
        }

        composeTestRule.onNodeWithText("Back").performClick()

        assertTrue(intent is NoteScreenIntent.NavigateBack)
    }

    @Test
    fun `NoteScreen triggers SaveNote intent when save button clicked`() {
        var intent: NoteScreenIntent? = null

        composeTestRule.setContent {
            NoteScreen(
                state = NoteScreenState(title = "Note", body = "Body"),
                onIntent = { intent = it }
            )
        }

        composeTestRule.onNodeWithText("Save").performClick()

        assertTrue(intent is NoteScreenIntent.SaveNote)
    }

    @Test
    fun `NoteScreen triggers ShowCategoryBottomSheet intent when category clicked`() {
        var intent: NoteScreenIntent? = null
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = "",
            color = ColorItemUIModel(color = NoteColor.BLUE)
        )

        composeTestRule.setContent {
            NoteScreen(
                state = NoteScreenState(
                    title = "Note",
                    body = "Body",
                    category = category
                ),
                onIntent = { intent = it }
            )
        }

        composeTestRule.onNodeWithText("💼 Work").performClick()

        assertTrue(intent is NoteScreenIntent.ShowCategoryBottomSheet)
    }

    @Test
    fun `CategorySelector displays category with correct emoji and name`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Personal",
            emoji = "🏠",
            description = "",
            color = ColorItemUIModel(color = NoteColor.GREEN)
        )

        composeTestRule.setContent {
            CategorySelector(
                category = category,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("🏠 Personal").assertExists()
    }

    @Test
    fun `CategorySelector triggers onClick when tapped`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = "",
            color = ColorItemUIModel(color = NoteColor.BLUE)
        )

        var clicked = false

        composeTestRule.setContent {
            CategorySelector(
                category = category,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("💼 Work").performClick()
        assertTrue(clicked)
    }
}
