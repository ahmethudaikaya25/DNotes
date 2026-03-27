package com.duhapp.dnotes.foundation.theme

import androidx.compose.ui.graphics.Color
import com.duhapp.dnotes.NoteColor

/**
 * Returns the compose variants for a given NoteColor enum.
 * @return Triple of (DarkColor, LightColor, TextColor)
 */
fun NoteColor.toComposeColors(): Triple<Color, Color, Color> {
    return when (this) {
        NoteColor.RED -> Triple(NoteColorRed, NoteColorRedLight, Color.White)
        NoteColor.ORANGE -> Triple(NoteColorOrange, NoteColorOrangeLight, Color.Black)
        NoteColor.YELLOW -> Triple(NoteColorYellow, NoteColorYellowLight, Color.Black)
        NoteColor.GREEN -> Triple(NoteColorGreen, NoteColorGreenLight, Color.Black)
        NoteColor.BLUE -> Triple(NoteColorBlue, NoteColorBlueLight, Color.White)
        NoteColor.PURPLE -> Triple(NoteColorPurple, NoteColorPurpleLight, Color.White)
        NoteColor.BROWN -> Triple(NoteColorBrown, NoteColorBrownLight, Color.White)
        NoteColor.CYAN -> Triple(NoteColorCyan, NoteColorCyanLight, Color.Black)
    }
}
