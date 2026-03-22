package com.duhapp.dnotes.features.note.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    state: NoteScreenState,
    onIntent: (NoteScreenIntent) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<CategoryUIModel> = emptyList()
) {
    val scrollState = rememberScrollState()
    val bottomSheetState = rememberModalBottomSheetState()

    when {
        state.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        }
        state.error != null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        else -> {
            val backgroundColor = getLightColorFromOrdinal(state.category.color.color.ordinal)

            Box(modifier = modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .padding(horizontal = 16.dp)
                        .padding(top = 24.dp, bottom = 16.dp)
                        .imePadding()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { onIntent(NoteScreenIntent.NavigateBack) },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }

                        IconButton(
                            onClick = { onIntent(NoteScreenIntent.SaveNote) },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save",
                                tint = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BasicTextField(
                        value = state.title,
                        onValueChange = { onIntent(NoteScreenIntent.UpdateTitle(it)) },
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        cursorBrush = SolidColor(Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box {
                                if (state.title.isEmpty()) {
                                    Text(
                                        text = "Title",
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BasicTextField(
                        value = state.body,
                        onValueChange = { onIntent(NoteScreenIntent.UpdateBody(it)) },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        ),
                        cursorBrush = SolidColor(Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        decorationBox = { innerTextField ->
                            Box {
                                if (state.body.isEmpty()) {
                                    Text(
                                        text = "Details",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Color.Black.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CategorySelector(
                        category = state.category,
                        onClick = { onIntent(NoteScreenIntent.ShowCategoryBottomSheet) }
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            if (state.showCategoryBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { onIntent(NoteScreenIntent.HideCategoryBottomSheet) },
                    sheetState = bottomSheetState,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    containerColor = Color.White
                ) {
                    CategoryBottomSheetContent(
                        selectedCategory = state.category,
                        categories = categories,
                        onCategorySelected = { category ->
                            onIntent(NoteScreenIntent.SelectCategory(category))
                        },
                        onDismiss = { onIntent(NoteScreenIntent.HideCategoryBottomSheet) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySelector(
    category: CategoryUIModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = getDarkColorFromOrdinal(category.color.color.ordinal)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .clickable(onClick = onClick)
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = "${category.emoji} ${category.name}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun getLightColorFromOrdinal(ordinal: Int): Color {
    return when (ordinal) {
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

fun getDarkColorFromOrdinal(ordinal: Int): Color {
    return when (ordinal) {
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
