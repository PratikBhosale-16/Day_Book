package com.pratikbhosale.daybook.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.pratikbhosale.daybook.data.local.BucketDao
import com.pratikbhosale.daybook.data.local.PageDao
import com.pratikbhosale.daybook.data.local.SettingsKeys
import com.pratikbhosale.daybook.data.local.TaskDao
import com.pratikbhosale.daybook.data.model.Bucket
import com.pratikbhosale.daybook.data.model.Page
import com.pratikbhosale.daybook.data.model.Task
import com.pratikbhosale.daybook.domain.model.RolloverMode
import com.pratikbhosale.daybook.domain.model.Settings
import com.pratikbhosale.daybook.domain.repository.DaybookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DaybookRepositoryImpl @Inject constructor(
    private val bucketDao: BucketDao,
    private val pageDao: PageDao,
    private val taskDao: TaskDao,
    private val dataStore: DataStore<Preferences>,
) : DaybookRepository {

    // ── Buckets ──────────────────────────────────────────────────────────────

    override fun observeBuckets(): Flow<List<Bucket>> =
        bucketDao.observeAll()

    override suspend fun getBucket(id: Long): Bucket? =
        bucketDao.getById(id)

    override suspend fun addBucket(bucket: Bucket): Long =
        bucketDao.insert(bucket)

    override suspend fun updateBucket(bucket: Bucket) =
        bucketDao.update(bucket)

    override suspend fun deleteNonDefaultBucket(id: Long) =
        bucketDao.deleteNonDefault(id)

    // ── Pages ─────────────────────────────────────────────────────────────────

    override fun observeActivePagesForBucket(bucketId: Long): Flow<List<Page>> =
        pageDao.observeActiveByBucket(bucketId)

    override suspend fun getPage(id: Long): Page? =
        pageDao.getById(id)

    override suspend fun addPage(page: Page): Long =
        pageDao.insert(page)

    override suspend fun updatePage(page: Page) =
        pageDao.update(page)

    override suspend fun deletePage(id: Long) =
        pageDao.deleteById(id)

    override suspend fun sealPage(id: Long, sealedAt: Long) =
        pageDao.seal(id, sealedAt)

    override suspend fun archivePage(id: Long, archivedAt: Long) =
        pageDao.archive(id, archivedAt)

    override suspend fun getArchivedPages(limit: Int, offset: Int): List<Page> =
        pageDao.getArchivedPage(limit, offset)

    // ── Tasks ─────────────────────────────────────────────────────────────────

    override fun observeTasksForPage(pageId: Long): Flow<List<Task>> =
        taskDao.observeByPage(pageId)

    override suspend fun getTasksForPage(pageId: Long): List<Task> =
        taskDao.getByPage(pageId)

    override suspend fun getTask(id: Long): Task? =
        taskDao.getById(id)

    override suspend fun addTask(task: Task): Long =
        taskDao.insert(task)

    override suspend fun updateTask(task: Task) =
        taskDao.update(task)

    override suspend fun deleteTask(id: Long) =
        taskDao.deleteById(id)

    override suspend fun setTaskDone(id: Long, isDone: Boolean, doneAt: Long?) =
        taskDao.setDone(id, isDone, doneAt)

    override suspend fun clearTaskReminder(id: Long) =
        taskDao.clearReminder(id)

    // ── Settings (DataStore) ──────────────────────────────────────────────────

    override fun observeSettings(): Flow<Settings> =
        dataStore.data.map { prefs -> prefs.toSettings() }

    override suspend fun updateSettings(settings: Settings) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.ROLLOVER_MODE] = settings.rolloverMode.name
            prefs[SettingsKeys.PAUSE_BEFORE_TURN] = settings.pauseBeforeTurn
            prefs[SettingsKeys.ARCHIVE_AUTO_CLEAR] = settings.archiveAutoClear
            prefs[SettingsKeys.ARCHIVE_RETENTION_DAYS] =
                settings.archiveRetentionDays ?: -1
            prefs[SettingsKeys.WIDGET_FADE_DELAY_MINUTES] = settings.widgetFadeDelayMinutes
            prefs[SettingsKeys.TASK_FONT_KEY] = settings.taskFontKey
            prefs[SettingsKeys.CUSTOM_FONT_URI] = settings.customFontUri ?: ""
            prefs[SettingsKeys.DEFAULT_REMINDER_TIME] = settings.defaultReminderTime
        }
    }

    private fun Preferences.toSettings(): Settings = Settings(
        rolloverMode = this[SettingsKeys.ROLLOVER_MODE]
            ?.let { runCatching { RolloverMode.valueOf(it) }.getOrDefault(RolloverMode.AUTO_CARRY) }
            ?: RolloverMode.AUTO_CARRY,
        pauseBeforeTurn = this[SettingsKeys.PAUSE_BEFORE_TURN] ?: true,
        archiveAutoClear = this[SettingsKeys.ARCHIVE_AUTO_CLEAR] ?: false,
        archiveRetentionDays = this[SettingsKeys.ARCHIVE_RETENTION_DAYS]
            ?.takeIf { it != -1 },
        widgetFadeDelayMinutes = this[SettingsKeys.WIDGET_FADE_DELAY_MINUTES] ?: 5,
        taskFontKey = this[SettingsKeys.TASK_FONT_KEY] ?: "default",
        customFontUri = this[SettingsKeys.CUSTOM_FONT_URI]?.ifEmpty { null },
        defaultReminderTime = this[SettingsKeys.DEFAULT_REMINDER_TIME] ?: "09:00",
    )
}
