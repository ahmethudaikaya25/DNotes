package com.duhapp.dnotes.foundation.theme

import androidx.compose.ui.graphics.Color
import com.duhapp.dnotes.NoteColor

data class NoteColorPalette(
    val accent: Color,
    val container: Color,
    val onAccent: Color,
    val onContainer: Color,
    val outline: Color
)

fun NoteColor.toPalette(): NoteColorPalette {
    return when (this) {
        NoteColor.RED -> NoteColorPalette(
            accent = NoteColorRed,
            container = NoteColorRedLight,
            onAccent = Color.White,
            onContainer = Color(0xFF452224),
            outline = Color(0x66A54244)
        )
        NoteColor.ORANGE -> NoteColorPalette(
            accent = NoteColorOrange,
            container = NoteColorOrangeLight,
            onAccent = Color.White,
            onContainer = Color(0xFF4A2B16),
            outline = Color(0x66A55F2F)
        )
        NoteColor.YELLOW -> NoteColorPalette(
            accent = NoteColorYellow,
            container = NoteColorYellowLight,
            onAccent = Color(0xFF2C2208),
            onContainer = Color(0xFF47380A),
            outline = Color(0x66957A24)
        )
        NoteColor.GREEN -> NoteColorPalette(
            accent = NoteColorGreen,
            container = NoteColorGreenLight,
            onAccent = Color.White,
            onContainer = Color(0xFF1F4130),
            outline = Color(0x66507F61)
        )
        NoteColor.BLUE -> NoteColorPalette(
            accent = NoteColorBlue,
            container = NoteColorBlueLight,
            onAccent = Color.White,
            onContainer = Color(0xFF1B3552),
            outline = Color(0x66406493)
        )
        NoteColor.PURPLE -> NoteColorPalette(
            accent = NoteColorPurple,
            container = NoteColorPurpleLight,
            onAccent = Color.White,
            onContainer = Color(0xFF38264D),
            outline = Color(0x666A4E88)
        )
        NoteColor.BROWN -> NoteColorPalette(
            accent = NoteColorBrown,
            container = NoteColorBrownLight,
            onAccent = Color.White,
            onContainer = Color(0xFF412A20),
            outline = Color(0x665F4335)
        )
        NoteColor.CYAN -> NoteColorPalette(
            accent = NoteColorCyan,
            container = NoteColorCyanLight,
            onAccent = Color.White,
            onContainer = Color(0xFF1C3E43),
            outline = Color(0x66447F87)
        )
    }
}

/**
 * Returns the compose variants for a given NoteColor enum.
 * @return Triple of (DarkColor, LightColor, TextColor)
 */
fun NoteColor.toComposeColors(): Triple<Color, Color, Color> {
    val palette = toPalette()
    return Triple(palette.accent, palette.container, palette.onContainer)
}
