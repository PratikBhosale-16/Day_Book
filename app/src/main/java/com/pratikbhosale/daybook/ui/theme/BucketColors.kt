package com.pratikbhosale.daybook.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.pratikbhosale.daybook.data.model.ColorKey

/**
 * Maps a [ColorKey] to its fill + ink pair from the DESIGN.md palette.
 * Always sourced from the named constants in [Color.kt] — no hardcoded hex here.
 */
@Stable
data class BucketColorPair(val fill: Color, val ink: Color)

@Composable
fun colorPairFor(colorKey: ColorKey): BucketColorPair = when (colorKey) {
    ColorKey.TODAY -> BucketColorPair(TodayFill, TodayInk)
    ColorKey.WEEK -> BucketColorPair(WeekFill, WeekInk)
    ColorKey.SOMEDAY -> BucketColorPair(SomedayFill, SomedayInk)
    ColorKey.CUSTOM1 -> BucketColorPair(Custom1Fill, Custom1Ink)
    ColorKey.CUSTOM2 -> BucketColorPair(Custom2Fill, Custom2Ink)
    ColorKey.CUSTOM3 -> BucketColorPair(Custom3Fill, Custom3Ink)
}
