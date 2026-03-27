package com.duhapp.dnotes.foundation.uicomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A reusable Composable for displaying a category badge.
 * Shows an emoji, a name, and is fully rounded.
 *
 * @param emoji The category emoji (e.g. "📚").
 * @param name The category name (e.g. "Work").
 * @param backgroundColor The background color of the chip (light variant).
 * @param textColor The text color for the name.
 * @param onClick Optional click listener.
 * @param modifier Modifier for layout styling.
 */
@Composable
fun CategoryChip(
    emoji: String,
    name: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    var rowModifier = modifier
        .clip(RoundedCornerShape(50.dp))
        .background(backgroundColor)
    
    if (onClick != null) {
        rowModifier = rowModifier.clickable(onClick = onClick)
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = rowModifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = emoji, 
            fontSize = 18.sp
        )
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
