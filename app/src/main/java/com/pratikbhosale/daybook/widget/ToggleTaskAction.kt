package com.pratikbhosale.daybook.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState

/**
 * Handles tap-to-complete from the widget task list.
 *
 * Security: the incoming [TASK_ID_KEY] is validated against the repository before any
 * write occurs. An unknown or invalid ID is silently discarded — per security.md,
 * "never trust an intent extra blindly."
 *
 * The repository write triggers [WidgetUpdateCoordinator.scheduleUpdate] automatically
 * (centralized in [DaybookRepositoryImpl]) — this callback does not call updateAll()
 * directly.
 */
class ToggleTaskAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val taskId = parameters[TASK_ID_KEY]
            ?: return  // missing key — discard silently

        val repository = context.widgetRepository()

        // Validate: task must exist in the repository before acting.
        val task = repository.getTask(taskId)
            ?: return  // unknown ID — discard silently (security.md)

        // Write through the repository. WidgetUpdateCoordinator (inside
        // DaybookRepositoryImpl) will schedule the debounced widget refresh.
        repository.setTaskDone(
            id = task.id,
            isDone = !task.isDone,
            doneAt = if (!task.isDone) System.currentTimeMillis() else null,
        )
    }

    companion object {
        val TASK_ID_KEY = ActionParameters.Key<Long>("task_id")
    }
}
