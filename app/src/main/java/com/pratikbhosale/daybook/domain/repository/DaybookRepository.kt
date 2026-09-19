package com.pratikbhosale.daybook.domain.repository

import com.pratikbhosale.daybook.data.model.Bucket
import com.pratikbhosale.daybook.data.model.Page
import com.pratikbhosale.daybook.data.model.Task
import com.pratikbhosale.daybook.domain.model.Settings
import kotlinx.coroutines.flow.Flow

/**
 * The single repository contract consumed by ViewModels, workers, and the widget.
 *
 * The widget and the app MUST use the same instance (provided by Hilt) so that they
 * always see the same state. Never let the widget keep its own copy — see TRD.
 */
interface DaybookRepository {

    // ── Buckets ──────────────────────────────────────────────────────────────

    fun observeBuckets(): Flow<List<Bucket>>
    suspend fun getBucket(id: Long): Bucket?
    suspend fun addBucket(bucket: Bucket): Long
    suspend fun updateBucket(bucket: Bucket)
    /** Only deletes non-default buckets. Safe to call on default buckets — no-op. */
    suspend fun deleteNonDefaultBucket(id: Long)

    // ── Pages ─────────────────────────────────────────────────────────────────

    fun observeActivePagesForBucket(bucketId: Long): Flow<List<Page>>
    suspend fun getPage(id: Long): Page?
    suspend fun addPage(page: Page): Long
    suspend fun updatePage(page: Page)
    suspend fun deletePage(id: Long)
    suspend fun sealPage(id: Long, sealedAt: Long)
    suspend fun archivePage(id: Long, archivedAt: Long)
    suspend fun getArchivedPages(limit: Int, offset: Int): List<Page>

    // ── Tasks ─────────────────────────────────────────────────────────────────

    fun observeTasksForPage(pageId: Long): Flow<List<Task>>
    suspend fun getTasksForPage(pageId: Long): List<Task>
    suspend fun getTask(id: Long): Task?
    suspend fun addTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Long)
    suspend fun setTaskDone(id: Long, isDone: Boolean, doneAt: Long?)
    suspend fun clearTaskReminder(id: Long)

    // ── Settings (DataStore) ──────────────────────────────────────────────────

    fun observeSettings(): Flow<Settings>
    suspend fun updateSettings(settings: Settings)
}
