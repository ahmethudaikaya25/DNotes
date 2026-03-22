package com.duhapp.dnotes.features.manage_category.ui

import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryShowType
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import org.junit.Assert.*
import org.junit.Test

class ManageCategoryScreenContractTest {

    @Test
    fun `ManageCategoryScreenState has correct default values`() {
        val state = ManageCategoryScreenState()
        
        assertFalse(state.isLoading)
        assertTrue(state.categories.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `ManageCategoryScreenState can be created with custom values`() {
        val categories = listOf(
            CategoryUIModel(
                id = 1,
                name = "Work",
                emoji = "💼",
                description = "Work tasks"
            ),
            CategoryUIModel(
                id = 2,
                name = "Personal",
                emoji = "🏠",
                description = "Personal notes"
            )
        )
        
        val state = ManageCategoryScreenState(
            isLoading = true,
            categories = categories,
            error = "Error message"
        )
        
        assertTrue(state.isLoading)
        assertEquals(2, state.categories.size)
        assertEquals("Work", state.categories[0].name)
        assertEquals("Personal", state.categories[1].name)
        assertEquals("Error message", state.error)
    }

    @Test
    fun `ManageCategoryScreenIntent LoadCategories is a singleton object`() {
        val intent1 = ManageCategoryScreenIntent.LoadCategories
        val intent2 = ManageCategoryScreenIntent.LoadCategories
        assertSame(intent1, intent2)
    }

    @Test
    fun `ManageCategoryScreenIntent CategoryClicked contains correct category`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val intent = ManageCategoryScreenIntent.CategoryClicked(category)
        assertEquals("Work", intent.category.name)
        assertEquals("💼", intent.category.emoji)
    }

    @Test
    fun `ManageCategoryScreenIntent AddCategoryClicked is a singleton object`() {
        val intent1 = ManageCategoryScreenIntent.AddCategoryClicked
        val intent2 = ManageCategoryScreenIntent.AddCategoryClicked
        assertSame(intent1, intent2)
    }

    @Test
    fun `ManageCategoryScreenIntent DeleteCategory contains correct category`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val intent = ManageCategoryScreenIntent.DeleteCategory(category)
        assertEquals("Work", intent.category.name)
    }

    @Test
    fun `ManageCategoryScreenEffect NavigateToCategoryBottomSheet contains correct data`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val effect = ManageCategoryScreenEffect.NavigateToCategoryBottomSheet(
            category = category,
            showType = CategoryShowType.Edit
        )
        
        assertEquals("Work", effect.category.name)
        assertEquals(CategoryShowType.Edit, effect.showType)
    }

    @Test
    fun `ManageCategoryScreenEffect NavigateToCategoryBottomSheet with Add type`() {
        val category = CategoryUIModel(
            id = -1,
            name = "",
            emoji = "",
            description = ""
        )
        val effect = ManageCategoryScreenEffect.NavigateToCategoryBottomSheet(
            category = category,
            showType = CategoryShowType.Add
        )
        
        assertEquals(CategoryShowType.Add, effect.showType)
    }

    @Test
    fun `ManageCategoryScreenEffect ShowError contains correct message`() {
        val effect = ManageCategoryScreenEffect.ShowError("Something went wrong")
        assertEquals("Something went wrong", effect.message)
    }

    @Test
    fun `ManageCategoryScreenEffect ShowDeleteConfirmation contains correct category`() {
        val category = CategoryUIModel(
            id = 1,
            name = "Work",
            emoji = "💼",
            description = ""
        )
        val effect = ManageCategoryScreenEffect.ShowDeleteConfirmation(category)
        assertEquals("Work", effect.category.name)
    }
}
