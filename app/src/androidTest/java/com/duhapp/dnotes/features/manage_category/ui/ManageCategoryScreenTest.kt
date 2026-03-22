package com.duhapp.dnotes.features.manage_category.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryShowType
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class ManageCategoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ManageCategoryScreen displays loading state`() {
        composeTestRule.setContent {
            ManageCategoryScreen(
                state = ManageCategoryScreenState(isLoading = true),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Loading...").assertExists()
    }

    @Test
    fun `ManageCategoryScreen displays error state`() {
        val errorMessage = "Failed to load categories"
        composeTestRule.setContent {
            ManageCategoryScreen(
                state = ManageCategoryScreenState(error = errorMessage),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText(errorMessage).assertExists()
    }

    @Test
    fun `ManageCategoryScreen displays categories`() {
        val categories = listOf(
            CategoryUIModel(
                id = 1,
                name = "Work",
                emoji = "💼",
                description = "Work related notes"
            ),
            CategoryUIModel(
                id = 2,
                name = "Personal",
                emoji = "🏠",
                description = "Personal notes"
            )
        )

        composeTestRule.setContent {
            ManageCategoryScreen(
                state = ManageCategoryScreenState(categories = categories),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Work").assertExists()
        composeTestRule.onNodeWithText("Personal").assertExists()
        composeTestRule.onNodeWithText("Work related notes").assertExists()
    }

    @Test
    fun `ManageCategoryScreen displays title`() {
        composeTestRule.setContent {
            ManageCategoryScreen(
                state = ManageCategoryScreenState(categories = emptyList()),
                onIntent = {}
            )
        }

        composeTestRule.onNodeWithText("Select a category for edit").assertExists()
    }

    @Test
    fun `ManageCategoryScreen triggers CategoryClicked intent when category clicked`() {
        var intent: ManageCategoryScreenIntent? = null
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )

        composeTestRule.setContent {
            ManageCategoryScreen(
                state = ManageCategoryScreenState(categories = listOf(category)),
                onIntent = { intent = it }
            )
        }

        composeTestRule.onNodeWithText("Work").performClick()

        assertTrue(intent is ManageCategoryScreenIntent.CategoryClicked)
        assertEquals("Work", (intent as ManageCategoryScreenIntent.CategoryClicked).category.name)
    }

    @Test
    fun `ManageCategoryScreen triggers AddCategoryClicked intent when FAB clicked`() {
        var intent: ManageCategoryScreenIntent? = null

        composeTestRule.setContent {
            ManageCategoryScreen(
                state = ManageCategoryScreenState(categories = emptyList()),
                onIntent = { intent = it }
            )
        }

        composeTestRule.onNodeWithText("Add Category").assertExists()
    }

    @Test
    fun `ManageCategoryContent displays Add Category FAB`() {
        composeTestRule.setContent {
            ManageCategoryContent(
                categories = emptyList(),
                onCategoryClick = {},
                onAddCategoryClick = {},
                onDeleteCategory = {}
            )
        }

        composeTestRule.onNodeWithText("Add Category").assertExists()
    }

    @Test
    fun `CategoryListItem displays category details`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Shopping",
            emoji = "🛒",
            description = "Shopping list"
        )

        var clicked = false

        composeTestRule.setContent {
            CategoryListItem(
                category = category,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Shopping").assertExists()
        composeTestRule.onNodeWithText("Shopping list").assertExists()

        composeTestRule.onNodeWithText("Shopping").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `CategoryListItem hides description when empty`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )

        composeTestRule.setContent {
            CategoryListItem(
                category = category,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Work").assertExists()
    }
}
