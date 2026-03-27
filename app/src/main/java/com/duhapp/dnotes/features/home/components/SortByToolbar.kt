package com.duhapp.dnotes.features.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duhapp.dnotes.features.home.SortBy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortByToolbar(
    currentSort: SortBy,
    onSortChanged: (SortBy) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
) {
    val sortOptions = listOf(
        SortBy.DATE_ADDED to "Date Added",
        SortBy.DATE_MODIFIED to "Modified",
        SortBy.TITLE to "Title",
        SortBy.COLOR to "Color"
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(
            items = sortOptions,
            key = { it.first }
        ) { (sortBy, label) ->
            FilterChip(
                selected = currentSort == sortBy,
                onClick = { onSortChanged(sortBy) },
                label = { Text(label) }
            )
        }
    }
}
