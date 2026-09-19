package com.pratikbhosale.daybook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A container that groups Pages. The three default buckets (Today, This week, Someday)
 * are seeded on first run. Users may add custom buckets up to three additional ones.
 *
 * [isDefault] marks the three system buckets that cannot be deleted.
 */
@Entity(tableName = "buckets")
data class Bucket(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorKey: ColorKey,
    val sortOrder: Int,
    val isDefault: Boolean,
)
