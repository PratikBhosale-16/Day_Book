package com.pratikbhosale.daybook.widget

/**
 * Snapshot of data the widget needs to render, persisted across process restarts
 * via [WidgetStateDefinition].
 *
 * This is the "last known good state" — if a live data fetch fails, the widget
 * re-renders this rather than showing a blank or error tile.
 *
 * Serialized manually via [WidgetStateSerializer] to avoid a new library dependency.
 * Fields are primitives or lists of primitives — deliberately kept flat.
 */
data class WidgetState(
    /** True once any successful fetch has populated this state. */
    val hasData: Boolean = false,
    val pageTitle: String = "",
    val bucketName: String = "",
    val taskTexts: List<String> = emptyList(),
    val taskIds: List<Long> = emptyList(),
    val taskDone: List<Boolean> = emptyList(),
    val doneTasks: Int = 0,
    val totalTasks: Int = 0,
) {
    companion object {
        val EMPTY = WidgetState()
    }
}
