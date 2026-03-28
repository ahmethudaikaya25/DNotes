package com.duhapp.dnotes.features.add_or_update_category.ui

import android.os.Parcelable
import com.duhapp.dnotes.NoteColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryUIModel(
    var id: Int = -1,
    val name: String = "",
    var emoji: String = "",
    val description: String = "",
    val color: ColorItemUIModel = ColorItemUIModel(),
    val isDefault: Boolean = false,
) : Parcelable {
    fun toEntity() = com.duhapp.dnotes.app.database.CategoryEntity(
        name = name,
        message = description,
        emoji = emoji,
        colorId = color.color.ordinal,
        isDefault = isDefault
    ).apply {
        this.id = if (this@CategoryUIModel.id > 0) this@CategoryUIModel.id else 0
    }
}

fun com.duhapp.dnotes.app.database.CategoryEntity.toUIModel() = CategoryUIModel(
    id = id,
    name = name,
    emoji = emoji,
    description = message,
    color = ColorItemUIModel(
        color = com.duhapp.dnotes.NoteColor.fromOrdinal(colorId)
    ),
    isDefault = isDefault
)
