package com.duhapp.dnotes.foundation.uicomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * A reusable Composable for displaying a note card with a gradient background,
 * a title, and a body preview.
 *
 * @param title The title of the note.
 * @param body The body content of the note.
 * @param colorDark The dark color for the top of the gradient.
 * @param colorLight The light color for the bottom of the gradient.
 * @param textColor The color of the text (depends on the contrast with background).
 * @param onClick Callback triggered when the card is pressed.
 * @param modifier Modifier for styling or layout.
 */
@Composable
fun NoteCard(
    title: String,
    body: String,
    colorDark: Color,
    colorLight: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(colorDark, colorLight)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(gradientBrush)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (body.isNotBlank()) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
