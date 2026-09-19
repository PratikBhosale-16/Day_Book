package com.pratikbhosale.daybook.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A container that groups Pages. The three default buckets (Today, This week, Someday)
 * are seeded on first run. Users may add custom buckets up to three additional ones.
 *
 * [isDefault] marks the three system buckets that cannot be deleted.
 *
 * [name] has a UNIQUE index so that INSERT OR IGNORE in [DatabaseSeeder] is genuinely
 * idempotent — a second seed run will conflict on name and be silently ignored, leaving
 * the row count unchanged.
 */
@Entity(
    tableName = "buckets",
    indices = [Index("name", unique = true)],
)
data class Bucket(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorKey: ColorKey,
    val sortOrder: Int,
    val isDefault: Boolean,
)
