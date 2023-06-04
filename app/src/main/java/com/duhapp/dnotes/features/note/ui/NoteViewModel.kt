package com.duhapp.dnotes.features.note.ui

import androidx.lifecycle.viewModelScope
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.base.ui.BaseViewModel
import com.duhapp.dnotes.features.base.ui.FragmentUIEvent
import com.duhapp.dnotes.features.base.ui.FragmentUIState
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BasicNoteUIModel
import com.duhapp.dnotes.features.note.domain.UpsertNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val upsertNote: UpsertNote,
) : BaseViewModel<NoteUIEvent, NoteUIState>() {

    init {
        mutableUIState.value = NoteUIState(
            baseNoteUIModel = BasicNoteUIModel(
                id = -1,
                isPinned = false,
                isCompleted = false,
                isCompletable = false,
                title = "",
                body = "",
                category = CategoryUIModel(
                    id = -1,
                    name = "",
                    emoji = "",
                    description = "",
                    colorId = -1,
                ),
                color = -1,
            ),
            editableMode = false,
        )
    }

    fun switchEditMode() {
        viewModelScope.launch {
            mutableUIState.value?.let {
                val noteModel = it.baseNoteUIModel
                if (mutableUIState.value?.editableMode == false) {
                    noteModel.id = upsertNote.invoke(noteModel)
                }
                mutableUIState.value = it.copy(
                    editableMode = !it.editableMode,
                    baseNoteUIModel = noteModel,
                )
            }
        }
    }

    fun onCategorySelected(category: CategoryUIModel) {
        println("onCategorySelected: $category")
        viewModelScope.launch {
            mutableUIState.value?.let {
                mutableUIState.value = it.copy(
                    baseNoteUIModel = it.baseNoteUIModel.apply {
                        this.category = category
                    },
                )
            }
        }
    }

    fun categorySelectClicked() {
        setEvent(NoteUIEvent.NavigateSelectCategory)
        setEvent(NoteUIEvent.Loading)
    }
}

sealed interface NoteUIEvent : FragmentUIEvent {
    object NavigateSelectCategory : NoteUIEvent
    object Loading : NoteUIEvent
}

data class NoteUIState(
    var baseNoteUIModel: BaseNoteUIModel = BasicNoteUIModel(
        id = -1,
        isPinned = false,
        isCompleted = false,
        isCompletable = false,
        title = "",
        body = "",
        category = CategoryUIModel(
            id = -1,
            name = "",
            emoji = "",
            description = "",
            colorId = -1,
        ),
        color = -1,
    ),
    var editableMode: Boolean = false,
) : FragmentUIState