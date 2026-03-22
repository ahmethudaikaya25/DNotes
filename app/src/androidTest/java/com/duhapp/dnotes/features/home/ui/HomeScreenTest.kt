package com.duhapp.dnotes.features.home.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.duhapp.dnotes.features.home.home_screen_category.ui.BasicNoteUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.HomeCategoryUIModel
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `HomeScreen displays loading state`() {
        composeTestRule.setContent {
            HomeScreen(
                state = HomeScreenState(isLoading = true),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Loading...").assertExists()
    }

    @Test
    fun `HomeScreen displays error state`() {
        val errorMessage = "Error loading categories"
        composeTestRule.setContent {
            HomeScreen(
                state = HomeScreenState(error = errorMessage),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText(errorMessage).assertExists()
    }

    @Test
    fun `HomeScreen displays categories`() {
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

        composeTestRule.setContent {
            HomeScreen(
                state = HomeScreenState(categories = categories),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Work").assertExists()
        composeTestRule.onNodeWithText("Personal").assertExists()
    }

    @Test
    fun `HomeScreen displays View All button`() {
        val categories = listOf(
            HomeCategoryUIModel(
                id = 1,
                title = "Work",
                noteList = emptyList()
            )
        )

        composeTestRule.setContent {
            HomeScreen(
                state = HomeScreenState(categories = categories),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("View All").assertExists()
    }

    @Test
    fun `HomeScreen triggers NoteClicked intent when note clicked`() {
        var intent: HomeScreenIntent? = null
        val categories = listOf(
            HomeCategoryUIModel(
                id = 1,
                title = "Work",
                noteList = listOf(
                    BasicNoteUIModel(
                        id = 42,
                        isPinned = false,
                        isCompleted = false,
                        isCompletable = false,
                        category = CategoryUIModel(id = 1, name = "Work", emoji = "💼", description = ""),
                        title = "Test Note",
                        body = "Body",
                        color = 0
                    )
                )
            )
        )

        composeTestRule.setContent {
            HomeScreen(
                state = HomeScreenState(categories = categories),
                onIntent = { intent = it }
            )
        }

        composeTestRule.onNodeWithText("Test Note").performClick()

        assertTrue(intent is HomeScreenIntent.NoteClicked)
        assertEquals(42, (intent as HomeScreenIntent.NoteClicked).noteId)
    }

    @Test
    fun `HomeScreen triggers ViewAllClicked intent when View All clicked`() {
        var intent: HomeScreenIntent? = null
        val categories = listOf(
            HomeCategoryUIModel(
                id = 1,
                title = "Work",
                noteList = emptyList()
            )
        )

        composeTestRule.setContent {
            HomeScreen(
                state = HomeScreenState(categories = categories),
                onIntent = { intent = it }
            )
        }

        composeTestRule.onNodeWithText("View All").performClick()

        assertTrue(intent is HomeScreenIntent.ViewAllClicked)
        assertEquals(1, (intent as HomeScreenIntent.ViewAllClicked).categoryId)
    }

    @Test
    fun `HomeContent displays notes`() {
        val notes = listOf(
            BasicNoteUIModel(
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
                body = "Test Body",
                color = 0
            )
        )

        val categories = listOf(
            HomeCategoryUIModel(
                id = 1,
                title = "Work",
                noteList = notes
            )
        )

        composeTestRule.setContent {
            HomeContent(
                categories = categories,
                onNoteClick = {},
                onViewAllClick = {}
            )
        }

        composeTestRule.onNodeWithText("Test Note").assertExists()
        composeTestRule.onNodeWithText("Work").assertExists()
    }

    @Test
    fun `NoteListItem displays note details`() {
        val note = BasicNoteUIModel(
            id = 1,
            isPinned = false,
            isCompleted = false,
            isCompletable = false,
            category = CategoryUIModel(
                id = 1,
                name = "Personal",
                emoji = "🏠",
                description = ""
            ),
            title = "Shopping List",
            body = "Buy milk, eggs, bread",
            color = 1
        )

        var clicked = false

        composeTestRule.setContent {
            NoteListItem(
                note = note,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Shopping List").assertExists()
        composeTestRule.onNodeWithText("Personal").assertExists()

        composeTestRule.onNodeWithText("Shopping List").performClick()
        assertTrue(clicked)
    }
}
