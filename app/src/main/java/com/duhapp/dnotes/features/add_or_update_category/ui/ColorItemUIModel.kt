package com.duhapp.dnotes.features.add_or_update_category.ui

import android.os.Parcelable
import com.duhapp.dnotes.NoteColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorItemUIModel(
    var isSelected: Boolean = false,
    val color: NoteColor = NoteColor.BLUE
) : Parcelable {
    fun newCopy(isSelected: Boolean = this.isSelected, color: NoteColor = this.color) =
        ColorItemUIModel(
            isSelected = isSelected,
            color = color
        )
}