package com.duhapp.dnotes.features.home.home_screen_category.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class HomeCategoryUIModel(
    val id: Int,
    val title: String,
    val noteList: List<BaseNoteUIModel>
): Parcelable