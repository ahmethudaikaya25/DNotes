package com.duhapp.dnotes.features.all_notes.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duhapp.dnotes.features.home.home_screen_category.ui.BasicNoteUIModel
import com.duhapp.dnotes.ui.theme.BackgroundColor
import com.duhapp.dnotes.ui.theme.PrimaryColor

private val AllNotesCardWidth = 180.dp
private val AllNotesCardMaxHeight = 252.dp
private val AllNotesGridSpacing = 16.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllNotesScreen(
    state: AllNotesScreenState,
    onIntent: (AllNotesScreenIntent) -> Unit,
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
            val headerColor = getCategoryColor(state.category.color.color.ordinal)

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerColor)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.category.emoji,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = state.category.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (state.notes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No notes found")
                    }
                } else {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        val maxItemsInRow = ((maxWidth + AllNotesGridSpacing) / (AllNotesCardWidth + AllNotesGridSpacing))
                            .toInt()
                            .coerceAtLeast(1)

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(AllNotesGridSpacing),
                            verticalArrangement = Arrangement.spacedBy(AllNotesGridSpacing),
                            maxItemsInEachRow = maxItemsInRow
                        ) {
                            state.notes.filterIsInstance<BasicNoteUIModel>().forEach { note ->
                                AllNotesNoteItem(
                                    note = note,
                                    isSelectable = state.isSelectable,
                                    isSelected = state.selectedNoteIds.contains(note.id),
                                    onClick = {
                                        onIntent(AllNotesScreenIntent.NoteClicked(note))
                                    },
                                    onLongClick = {
                                        onIntent(AllNotesScreenIntent.NoteLongClicked(note))
                                    },
                                    onEdit = {
                                        onIntent(AllNotesScreenIntent.NoteClicked(note))
                                    },
                                    onDelete = {
                                        onIntent(AllNotesScreenIntent.DeleteNote(note))
                                    },
                                    onMove = {
                                        onIntent(AllNotesScreenIntent.MoveNote(note))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllNotesNoteItem(
    note: BasicNoteUIModel,
    isSelectable: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val backgroundColor = getNoteColor(note.color)
    val categoryBackgroundColor = getCategoryColor(note.color)

    Card(
        modifier = modifier
            .width(AllNotesCardWidth)
            .heightIn(max = AllNotesCardMaxHeight)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color.Blue, RoundedCornerShape(12.dp))
                } else Modifier
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                if (isSelectable && !isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                } else if (isSelectable && isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Blue)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = Color.White, fontSize = 12.sp)
                    }
                }

                Column {
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
                        color = Color.Black.copy(alpha = 0.7f),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(categoryBackgroundColor),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = note.category.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isSelectable) {
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Menu",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    onClick = {
                                        showMenu = false
                                        onEdit()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Move") },
                                    onClick = {
                                        showMenu = false
                                        onMove()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        showMenu = false
                                        onDelete()
                                    }
                                )
                            }
                        }
                    }
                }
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

fun getCategoryColor(colorOrdinal: Int): Color {
    return when (colorOrdinal) {
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
