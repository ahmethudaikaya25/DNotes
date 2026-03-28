package com.duhapp.dnotes.features.home.home_screen_category.ui

import android.os.Parcelable
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.NoteType
import com.duhapp.dnotes.R
import com.duhapp.dnotes.app.database.NoteEntity
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.add_or_update_category.ui.ColorItemUIModel
import kotlinx.parcelize.Parcelize

sealed class BaseNoteUIModel(
    open var id: Int = -1,
    open val type: NoteType = NoteType.BasicNote,
    open var isPinned: Boolean = false,
    open var isCompleted: Boolean = false,
    open var isCompletable: Boolean = false,
    open var category: CategoryUIModel = CategoryUIModel(),
    open var title: String = "",
    open var body: String = "",
    open var image: String = "",
    open var colorCode: NoteColor = NoteColor.RED,
    open var isSelected: Boolean = false,
    open var isSelectable: Boolean = false,
) : Parcelable {
    fun newCopy(): BaseNoteUIModel {
        return when (this) {
            is BasicNoteUIModel -> this.copy()
            is ImageNoteUIModel -> this.copy()
        }
    }
}

@Parcelize
data class BasicNoteUIModel(
    override var id: Int = -1,
    override var isPinned: Boolean = false,
    override var isCompleted: Boolean = false,
    override var isCompletable: Boolean = false,
    override var category: CategoryUIModel = CategoryUIModel(),
    override var title: String = "",
    override var body: String = "",
    override var colorCode: NoteColor = NoteColor.RED,
    override var isSelected: Boolean = false,
    override var isSelectable: Boolean = false,
) : BaseNoteUIModel(
    id = id,
    type = NoteType.BasicNote,
    isPinned = isPinned,
    isCompleted = isCompleted,
    isCompletable = isCompletable,
    category = category,
    title = title,
    body = body,
    image = "",
    colorCode = colorCode,
    isSelected = isSelected,
    isSelectable = isSelectable,
)

@Parcelize
data class ImageNoteUIModel(
    override var id: Int = -1,
    override var isPinned: Boolean = false,
    override var isCompleted: Boolean = false,
    override var isCompletable: Boolean = false,
    override var category: CategoryUIModel = CategoryUIModel(),
    override var title: String = "",
    override var body: String = "",
    override var image: String = "",
    override var colorCode: NoteColor = NoteColor.RED,
    override var isSelected: Boolean = false,
    override var isSelectable: Boolean = false,
) : BaseNoteUIModel(
    id = id,
    type = NoteType.ImageNote,
    isPinned = isPinned,
    isCompleted = isCompleted,
    isCompletable = isCompletable,
    category = category,
    title = title,
    body = body,
    image = image,
    colorCode = colorCode,
    isSelected = isSelected,
    isSelectable = isSelectable,
)

val DEFAULT_NOTE_MODEL = BasicNoteUIModel(
    id = -1,
    isPinned = false,
    isCompleted = false,
    isCompletable = false,
    category = CategoryUIModel(),
    title = "",
    body = "",
    colorCode = NoteColor.RED
)

fun NoteEntity.toUIModel(category: CategoryUIModel) = BasicNoteUIModel(
    id = id,
    isPinned = false,
    isCompleted = false,
    isCompletable = false,
    category = category,
    title = title,
    body = details,
    colorCode = category.color.color
)

fun BaseNoteUIModel.toEntity() = NoteEntity(
    title = title,
    details = body,
    categoryId = category.id,
).apply {
    this.id = this@toEntity.id
}
