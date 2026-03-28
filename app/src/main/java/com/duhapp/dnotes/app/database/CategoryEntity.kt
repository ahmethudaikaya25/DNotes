package com.duhapp.dnotes.app.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.duhapp.dnotes.NoteColor

@Entity(tableName = "CategoryEntity")
data class CategoryEntity(
    @ColumnInfo(name = "title") val name: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "emoji") val emoji: String,
    @ColumnInfo(name = "color_id") val colorId: Int,
    @ColumnInfo(name = "is_default") val isDefault: Boolean = false,
    @ColumnInfo(name = "sort_order") val sortOrder: Int = 0
) {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0
}