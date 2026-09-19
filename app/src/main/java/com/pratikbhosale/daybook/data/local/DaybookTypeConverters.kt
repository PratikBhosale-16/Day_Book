package com.pratikbhosale.daybook.data.local

import androidx.room.TypeConverter
import com.pratikbhosale.daybook.data.model.ColorKey

/**
 * Converts [ColorKey] enum to/from its String name for Room storage.
 * Using the name (not ordinal) is resilient to enum reordering.
 */
class DaybookTypeConverters {

    @TypeConverter
    fun colorKeyToString(value: ColorKey): String = value.name

    @TypeConverter
    fun stringToColorKey(value: String): ColorKey = ColorKey.valueOf(value)
}
