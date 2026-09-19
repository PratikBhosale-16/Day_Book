package com.pratikbhosale.daybook.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single task on a [Page].
 *
 * [reminderAt] and [reminderRequestCode] are nullable; only set when the user adds
 * a reminder. The request code is stored so the alarm can be cancelled precisely when
 * the task is edited, completed, or deleted (see TRD: "Orphaned alarms firing for
 * deleted tasks is a real and embarrassing bug — guard against it explicitly").
 *
 * Index on [pageId] and [sortOrder] per performance.md.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Page::class,
            parentColumns = ["id"],
            childColumns = ["pageId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("pageId"),
        Index("sortOrder"),
    ],
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pageId: Long,
    val text: String,
    val isDone: Boolean = false,
    val doneAt: Long? = null,             // epoch millis; null until done
    val sortOrder: Int,
    val reminderAt: Long? = null,         // epoch millis; null if no reminder
    val reminderRequestCode: Int? = null, // AlarmManager request code; null if no alarm
)
