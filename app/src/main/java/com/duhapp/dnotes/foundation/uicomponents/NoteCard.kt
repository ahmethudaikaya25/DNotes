package com.duhapp.dnotes.foundation.uicomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
 * @param isSelected Whether the note is currently selected.
 * @param isPinned Whether the note is pinned.
 * @param onClick Callback triggered when the card is pressed.
 * @param onLongClick Optional callback triggered when the card is long-pressed.
 * @param modifier Modifier for styling or layout.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    title: String,
    body: String,
    colorDark: Color,
    colorLight: Color,
    textColor: Color,
    isSelected: Boolean = false,
    isPinned: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(targetValue = if (isSelected) 0.94f else 1f, label = "selection_scale")

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(colorDark, colorLight)
    )

    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(gradientBrush)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
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
        
// Selection overlay and icon
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.matchParentSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(8.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color.White
                )
            }
        }

        // Pinned icon badge
        if (isPinned && !isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    imageVector = Icons.Filled.PushPin,
                    contentDescription = "Pinned",
                    tint = textColor.copy(alpha = 0.5f),
                    modifier = Modifier.scale(0.8f) // Make it slightly smaller 
                )
            }
        }
    }
}
