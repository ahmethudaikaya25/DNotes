package com.duhapp.dnotes.features.note.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.manage_category.ui.ManageCategoryViewModel
import com.duhapp.dnotes.ui.theme.BackgroundColor

@Composable
fun CategoryBottomSheetContent(
    selectedCategory: CategoryUIModel,
    onCategorySelected: (CategoryUIModel) -> Unit,
    onDismiss: () -> Unit,
    viewModel: ManageCategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = (uiState as? com.duhapp.dnotes.features.manage_category.ui.ManageCategoryUIState.Success)?.categoryList ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Category",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundColor, RoundedCornerShape(8.dp))
        ) {
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    isSelected = category.id == selectedCategory.id,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: CategoryUIModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = getDarkColorFromOrdinal(category.color.color.ordinal)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                if (isSelected) backgroundColor.copy(alpha = 0.3f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(
            text = "${category.emoji} ${category.name}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = if (isSelected) backgroundColor else Color.Black,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}
