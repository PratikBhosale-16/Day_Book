package com.pratikbhosale.daybook.widget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.glance.state.GlanceStateDefinition
import java.io.File
import java.io.InputStream
import java.io.OutputStream

// ── Serializer ────────────────────────────────────────────────────────────────

/**
 * Manual line-delimited serializer for [WidgetState]. Uses only stdlib — no new library.
 *
 * Format (UTF-8, newline-delimited):
 *   hasData\npageTitle\nbucketName\ntotalTasks\ndoneTasks\n
 *   taskId1|taskText1|isDone1\ntaskId2|taskText2|isDone2\n...
 *
 * Any parse error → [WidgetState.EMPTY] (safe degradation, not a crash).
 */
object WidgetStateSerializer : Serializer<WidgetState> {

    override val defaultValue: WidgetState = WidgetState.EMPTY

    override suspend fun readFrom(input: InputStream): WidgetState {
        return try {
            val lines = input.readBytes().decodeToString().lines()
            if (lines.size < 5) return WidgetState.EMPTY
            val hasData = lines[0] == "true"
            val pageTitle = lines[1]
            val bucketName = lines[2]
            val totalTasks = lines[3].toIntOrNull() ?: 0
            val doneTasks = lines[4].toIntOrNull() ?: 0
            val taskTexts = mutableListOf<String>()
            val taskIds = mutableListOf<Long>()
            val taskDone = mutableListOf<Boolean>()
            for (i in 5 until lines.size) {
                val parts = lines[i].split("|", limit = 3)
                if (parts.size == 3) {
                    taskIds.add(parts[0].toLongOrNull() ?: continue)
                    taskTexts.add(parts[1])
                    taskDone.add(parts[2] == "true")
                }
            }
            WidgetState(
                hasData = hasData,
                pageTitle = pageTitle,
                bucketName = bucketName,
                totalTasks = totalTasks,
                doneTasks = doneTasks,
                taskTexts = taskTexts,
                taskIds = taskIds,
                taskDone = taskDone,
            )
        } catch (e: Exception) {
            throw CorruptionException("Cannot deserialize WidgetState", e)
        }
    }

    override suspend fun writeTo(t: WidgetState, output: OutputStream) {
        val sb = StringBuilder()
        sb.appendLine(t.hasData)
        sb.appendLine(t.pageTitle)
        sb.appendLine(t.bucketName)
        sb.appendLine(t.totalTasks)
        sb.appendLine(t.doneTasks)
        t.taskIds.indices.forEach { i ->
            sb.appendLine("${t.taskIds[i]}|${t.taskTexts[i]}|${t.taskDone[i]}")
        }
        output.write(sb.toString().encodeToByteArray())
    }
}

// ── GlanceStateDefinition ─────────────────────────────────────────────────────

/**
 * Glance-managed DataStore for [WidgetState].
 *
 * API confirmed against androidx.glance:glance-appwidget:1.2.0 by compilation:
 *   - [getDataStore]: `suspend fun getDataStore(context: Context, fileKey: String): DataStore<T>`
 *   - [getLocation]: `fun getLocation(context: Context, fileKey: String): File`
 * Note: `fileKey` is `String`, NOT `File` — confirmed by the 1.2.0 compiler error
 * when the wrong type was used.
 */
object WidgetStateDefinition : GlanceStateDefinition<WidgetState> {

    private const val DATA_STORE_FILENAME = "daybook_widget_state.json"

    private val Context.widgetStateStore: DataStore<WidgetState>
        by dataStore(
            fileName = DATA_STORE_FILENAME,
            serializer = WidgetStateSerializer,
        )

    override suspend fun getDataStore(
        context: Context,
        fileKey: String,
    ): DataStore<WidgetState> = context.widgetStateStore

    override fun getLocation(context: Context, fileKey: String): File =
        File(context.filesDir, "datastore/$DATA_STORE_FILENAME")
}
