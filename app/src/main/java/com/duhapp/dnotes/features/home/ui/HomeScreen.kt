package com.duhapp.dnotes.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duhapp.dnotes.features.home.home_screen_category.ui.BaseNoteUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.BasicNoteUIModel
import com.duhapp.dnotes.features.home.home_screen_category.ui.HomeCategoryUIModel
import com.duhapp.dnotes.ui.theme.BackgroundColor
import com.duhapp.dnotes.ui.theme.PrimaryColor

private val HomeNoteCardWidth = 180.dp
private val HomeNoteCardHeight = 252.dp
private val HomeCategorySpacing = 16.dp

@Composable
fun HomeScreen(
    state: HomeScreenState,
    onIntent: (HomeScreenIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.isLoading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(BackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        }
        state.error != null -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(BackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        else -> {
            HomeContent(
                categories = state.categories,
                onNoteClick = { note ->
                    onIntent(HomeScreenIntent.NoteClicked(note.id))
                },
                onViewAllClick = { category ->
                    onIntent(HomeScreenIntent.ViewAllClicked(category.id))
                },
                modifier = modifier
            )
        }
    }
}

@Composable
fun HomeContent(
    categories: List<HomeCategoryUIModel>,
    onNoteClick: (BaseNoteUIModel) -> Unit,
    onViewAllClick: (HomeCategoryUIModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No notes yet. Tap + to create one!",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            categories.forEach { category ->
                HomeCategorySection(
                    category = category,
                    onNoteClick = onNoteClick,
                    onViewAllClick = onViewAllClick
                )
            }
        }
    }
}

@Composable
fun HomeCategorySection(
    category: HomeCategoryUIModel,
    onNoteClick: (BaseNoteUIModel) -> Unit,
    onViewAllClick: (HomeCategoryUIModel) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "View All",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = PrimaryColor,
                modifier = Modifier.clickable { onViewAllClick(category) }
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(HomeCategorySpacing)
        ) {
            items(
                items = category.noteList,
                key = { note -> note.id }
            ) { note ->
                if (note is BasicNoteUIModel) {
                    NoteListItem(
                        note = note,
                        onClick = { onNoteClick(note) }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteListItem(
    note: BasicNoteUIModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = getNoteColor(note.color)
    val categoryBackgroundColor = getCategoryColor(note.color)

    Card(
        modifier = modifier
            .width(HomeNoteCardWidth)
            .height(HomeNoteCardHeight)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = note.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = note.body,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(categoryBackgroundColor),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = note.category.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}

fun getNoteColor(colorRes: Int): Color {
    return when (colorRes) {
        0 -> Color(0xFFFFCDD2)
        1 -> Color(0xFFC8E6C9)
        2 -> Color(0xFFBBDEFB)
        3 -> Color(0xFFFFF9C4)
        4 -> Color(0xFFE1BEE7)
        5 -> Color(0xFFB2EBF2)
        6 -> Color(0xFFD7CCC8)
        7 -> Color(0xFFFFE0B2)
        else -> Color(0xFFFFFFFF)
    }
}

fun getCategoryColor(colorRes: Int): Color {
    return when (colorRes) {
        0 -> Color(0xFFD32F2F)
        1 -> Color(0xFF388E3C)
        2 -> Color(0xFF1976D2)
        3 -> Color(0xFFFBC02D)
        4 -> Color(0xFF7B1FA2)
        5 -> Color(0xFF0097A7)
        6 -> Color(0xFF5D4037)
        7 -> Color(0xFFF57C00)
        else -> Color(0xFF4CAF50)
    }
}
