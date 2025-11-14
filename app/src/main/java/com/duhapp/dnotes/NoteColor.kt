package com.duhapp.dnotes

import androidx.annotation.ColorRes
import androidx.compose.ui.graphics.Color
import com.duhapp.dnotes.ui.theme.NoteColors

enum class NoteColor(
    @ColorRes val darkColor: Int = R.color.note_color_red_dark,
    @ColorRes val lightColor: Int = R.color.note_color_red_light,
    @ColorRes val textColor: Int = R.color.white,
    @ColorRes val darkTextColor: Int = R.color.black,
    val composeColorDark: Color = NoteColors.RedDark,
    val composeColorLight: Color = NoteColors.RedLight,
    val composeTextColor: Color = Color.White,
    val composeDarkTextColor: Color = Color.Black
) {
    RED(
        R.color.note_color_red_dark, R.color.note_color_red_light,
        R.color.white, R.color.black,
        NoteColors.RedDark, NoteColors.RedLight,
        Color.White, Color.Black
    ),
    ORANGE(
        R.color.note_color_orange_dark, R.color.note_color_orange_light,
        R.color.black, R.color.black,
        NoteColors.OrangeDark, NoteColors.OrangeLight,
        Color.Black, Color.Black
    ),
    YELLOW(
        R.color.note_color_yellow_dark, R.color.note_color_yellow_light,
        R.color.black, R.color.black,
        NoteColors.YellowDark, NoteColors.YellowLight,
        Color.Black, Color.Black
    ),
    GREEN(
        R.color.note_color_green_dark, R.color.note_color_green_light,
        R.color.black, R.color.black,
        NoteColors.GreenDark, NoteColors.GreenLight,
        Color.Black, Color.Black
    ),
    BLUE(
        R.color.note_color_blue_dark, R.color.note_color_blue_light,
        R.color.white, R.color.black,
        NoteColors.BlueDark, NoteColors.BlueLight,
        Color.White, Color.Black
    ),
    PURPLE(
        R.color.note_color_purple_dark, R.color.note_color_purple_light,
        R.color.white, R.color.black,
        NoteColors.PurpleDark, NoteColors.PurpleLight,
        Color.White, Color.Black
    ),
    BROWN(
        R.color.note_color_brown_dark, R.color.note_color_brown_light,
        R.color.white, R.color.black,
        NoteColors.BrownDark, NoteColors.BrownLight,
        Color.White, Color.Black
    ),
    CYAN(
        R.color.note_color_cyan_dark, R.color.note_color_cyan_light,
        R.color.black, R.color.black,
        NoteColors.CyanDark, NoteColors.CyanLight,
        Color.Black, Color.Black
    );

    companion object {
        fun fromOrdinal(ordinal: Int): NoteColor {
            return values()[ordinal]
        }
    }
}