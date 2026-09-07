package com.duhapp.dnotes.features.add_or_update_category.ui

import androidx.annotation.StringRes
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.foundation.mvi.UiEffect
import com.duhapp.dnotes.foundation.mvi.UiIntent
import com.duhapp.dnotes.foundation.mvi.UiState

data class CategoryAddEditState(
    val category: CategoryUIModel = CategoryUIModel(),
    /** Snapshot taken on [CategoryAddEditIntent.Init], used to detect edits. */
    val initialCategory: CategoryUIModel = CategoryUIModel(),
    val colors: List<ColorItemUIModel> = emptyList(),
    val showType: CategoryShowType = CategoryShowType.Add,
    val hasSelectedEmoji: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** Error coming from the domain layer, which reports failures as string resources. */
    @StringRes val errorMessageRes: Int? = null
) : UiState {

    /** True when every field the user has to provide is filled in. */
    val isFilled: Boolean
        get() = category.name.isNotBlank() &&
            category.description.isNotBlank() &&
            category.emoji.isNotBlank()

    /**
     * Compares each editable field on its own instead of the whole model, because
     * [CategoryUIModel] and [ColorItemUIModel] carry mutable/ui-only fields (id,
     * isSelected) that must not take part in the comparison. A color-only change
     * therefore counts as a change just like a renamed category does.
     */
    val hasChanges: Boolean
        get() = category.name != initialCategory.name ||
            category.description != initialCategory.description ||
            category.emoji != initialCategory.emoji ||
            category.color.color != initialCategory.color.color ||
            category.isDefault != initialCategory.isDefault

    /** Saving is only allowed for a complete category that actually differs from the stored one. */
    val canSave: Boolean
        get() = isFilled && hasChanges && !isLoading
}

sealed interface CategoryAddEditIntent : UiIntent {
    data class Init(val category: CategoryUIModel?, val showType: CategoryShowType) : CategoryAddEditIntent
    data class UpdateName(val name: String) : CategoryAddEditIntent
    data class UpdateDescription(val description: String) : CategoryAddEditIntent
    data class UpdateEmoji(val emoji: String) : CategoryAddEditIntent
    data class SelectColor(val color: NoteColor) : CategoryAddEditIntent
    object SaveCategory : CategoryAddEditIntent

    /**
     * @param newDefaultCategoryId The category that takes over the default role, required
     * when the category being deleted is the default one.
     */
    data class DeleteCategory(val newDefaultCategoryId: Int? = null) : CategoryAddEditIntent
    object Dismiss : CategoryAddEditIntent
}

sealed interface CategoryAddEditEffect : UiEffect {
    object CategorySaved : CategoryAddEditEffect
    data class CategoryDeleted(val categoryName: String) : CategoryAddEditEffect
    object Dismiss : CategoryAddEditEffect
    data class ShowError(val message: String) : CategoryAddEditEffect
}
