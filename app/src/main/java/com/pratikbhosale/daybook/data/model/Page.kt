package com.pratikbhosale.daybook.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single notebook page inside a [Bucket].
 *
 * Progress (done tasks / total tasks) is *derived*, never stored — see TRD.
 *
 * [sealedAt] is null until the user marks the page complete.
 * [archivedAt] is null until the page is moved to the archive (by rollover or manually).
 *
 * Indices on [bucketId] and [sortOrder] per performance.md.
 * Index on [archivedAt] because the archive screen queries by it (and must paginate).
 */
@Entity(
    tableName = "pages",
    foreignKeys = [
        ForeignKey(
            entity = Bucket::class,
            parentColumns = ["id"],
            childColumns = ["bucketId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("bucketId"),
        Index("sortOrder"),
        Index("archivedAt"),
    ],
)
data class Page(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bucketId: Long,
    val title: String,
    val sortOrder: Int,
    val createdAt: Long,          // epoch millis
    val sealedAt: Long? = null,   // epoch millis; null until sealed
    val archivedAt: Long? = null, // epoch millis; null until archived
)
