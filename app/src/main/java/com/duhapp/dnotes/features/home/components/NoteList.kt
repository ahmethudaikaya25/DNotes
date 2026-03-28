package com.duhapp.dnotes.features.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duhapp.dnotes.NoteColor
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.foundation.theme.toComposeColors
import com.duhapp.dnotes.foundation.uicomponents.NoteCard

@Composable
fun NoteList(
    notes: List<BaseNoteUIModel>,
    onNoteClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp
    ) {
        items(
            items = notes,
            key = { note -> note.id }
        ) { note ->
            val noteColorEnum = note.colorCode
            val (colorDark, colorLight, textColor) = noteColorEnum.toComposeColors()

            NoteCard(
                title = note.title,
                body = note.body,
                colorDark = colorDark,
                colorLight = colorLight,
                textColor = textColor,
                isPinned = note.isPinned,
                onClick = { onNoteClick(note.id) }
            )
        }
    }
}
