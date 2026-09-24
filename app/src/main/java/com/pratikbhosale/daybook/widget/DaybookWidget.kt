package com.pratikbhosale.daybook.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.pratikbhosale.daybook.R
import kotlinx.coroutines.flow.first

/**
 * The Daybook home-screen widget.
 *
 * Renders the first active page in the "Today" bucket: title, progress ring, task list.
 * Degrades gracefully at smaller sizes by showing fewer tasks — never clips text.
 *
 * Update mechanism: push-only via [WidgetUpdateCoordinator], centralized in
 * [DaybookRepositoryImpl]. No [updatePeriodMillis] polling.
 *
 * Error fallback: on a failed fetch the widget re-renders the last persisted
 * [WidgetState] (true last-known-good). Only if no prior state exists does it
 * show a placeholder.
 */
class DaybookWidget : GlanceAppWidget() {

    override val stateDefinition = WidgetStateDefinition

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(110.dp, 110.dp),  // 2×2 minimum
            DpSize(220.dp, 110.dp),  // 4×2 wide
            DpSize(110.dp, 220.dp),  // 2×4 tall
            DpSize(220.dp, 220.dp),  // 4×4 large
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Attempt a live data fetch; on success persist the new state.
        val freshState = fetchState(context)
        if (freshState != null) {
            updateAppWidgetState(context, WidgetStateDefinition, id) { freshState }
        }
        // currentState<WidgetState>() inside provideContent reads the persisted state —
        // either the just-updated fresh state, or the previous good state on error.
        provideContent {
            val state = currentState<WidgetState>()
            val size = LocalSize.current

            // Header ≈ 72dp (ring+title row 48dp + spacer 10dp + label 14dp).
            // Each task row ≈ 40dp. Calculate how many fit without clipping.
            val availableForTasksDp = (size.height.value - HEADER_HEIGHT_DP).toInt()
            val maxVisibleTasks = (availableForTasksDp / TASK_ROW_HEIGHT_DP).coerceAtLeast(0)

            WidgetContent(state = state, maxTasks = maxVisibleTasks)
        }
    }

    /**
     * Fetches live data from the repository. Returns null on any error so the
     * caller can fall back to last-known-good persisted state.
     */
    private suspend fun fetchState(context: Context): WidgetState? {
        return try {
            val repository = context.widgetRepository()
            val buckets = repository.observeBuckets().first()
            val todayBucket = buckets.minByOrNull { it.sortOrder } ?: return null
            val pages = repository.observeActivePagesForBucket(todayBucket.id).first()
            if (pages.isEmpty()) {
                return WidgetState(
                    hasData = true,
                    bucketName = todayBucket.name,
                )
            }
            val page = pages.first()
            val tasks = repository.getTasksForPage(page.id)
            val done = tasks.count { it.isDone }
            WidgetState(
                hasData = true,
                pageTitle = page.title,
                bucketName = todayBucket.name,
                taskTexts = tasks.map { it.text },
                taskIds = tasks.map { it.id },
                taskDone = tasks.map { it.isDone },
                doneTasks = done,
                totalTasks = tasks.size,
            )
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val HEADER_HEIGHT_DP = 72f
        private const val TASK_ROW_HEIGHT_DP = 40  // Int so division stays Int
    }
}

// ── Glance composables ────────────────────────────────────────────────────────

@Composable
private fun WidgetContent(state: WidgetState, maxTasks: Int) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(R.color.widget_background))
            .padding(12.dp),
    ) {
        if (!state.hasData) {
            PlaceholderContent()
        } else {
            // ── Header: progress ring + page title ───────────────────────────
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val ringRes = WidgetRingSelector.ringDrawableRes(state.doneTasks, state.totalTasks)
                Image(
                    provider = ImageProvider(ringRes),
                    contentDescription = "${state.doneTasks} of ${state.totalTasks} tasks done",
                    modifier = GlanceModifier.size(40.dp),
                )
                Spacer(GlanceModifier.width(8.dp))
                Column {
                    Text(
                        text = state.bucketName,
                        style = TextStyle(
                            color = ColorProvider(R.color.widget_ink_muted),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        maxLines = 1,
                    )
                    Text(
                        text = state.pageTitle.ifEmpty { "No active page" },
                        style = TextStyle(
                            color = ColorProvider(R.color.widget_ink),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        maxLines = 1,
                    )
                }
            }

            Spacer(GlanceModifier.height(10.dp))

            // ── Task list (capped to maxTasks to prevent text clipping) ──────
            val visibleIndices = state.taskIds.indices.take(maxTasks)

            if (state.totalTasks == 0) {
                Text(
                    text = "No tasks yet",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_ink_muted),
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                    ),
                )
            } else {
                visibleIndices.forEach { i ->
                    TaskRow(
                        text = state.taskTexts[i],
                        isDone = state.taskDone[i],
                        taskId = state.taskIds[i],
                    )
                }
                val hidden = state.totalTasks - visibleIndices.size
                if (hidden > 0) {
                    Spacer(GlanceModifier.height(4.dp))
                    Text(
                        text = "+$hidden more",
                        style = TextStyle(
                            color = ColorProvider(R.color.widget_ink_muted),
                            fontSize = 11.sp,
                        ),
                    )
                }
            } // end inner task-list if/else
        } // end outer hasData else
    } // end Column
}

@Composable
private fun PlaceholderContent() {
    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Tap to open Daybook",
            style = TextStyle(
                color = ColorProvider(R.color.widget_ink_muted),
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
            ),
        )
    }
}

@Composable
private fun TaskRow(text: String, isDone: Boolean, taskId: Long) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val circleRes = if (isDone) R.drawable.ic_ring_complete else R.drawable.ic_ring_0_of_1
        Image(
            provider = ImageProvider(circleRes),
            contentDescription = if (isDone) "Done" else "Not done",
            modifier = GlanceModifier
                .size(18.dp)
                .clickable(
                    actionRunCallback<ToggleTaskAction>(
                        actionParametersOf(ToggleTaskAction.TASK_ID_KEY to taskId)
                    )
                ),
        )
        Spacer(GlanceModifier.width(8.dp))
        Text(
            text = text,
            style = TextStyle(
                color = if (isDone)
                    ColorProvider(R.color.widget_ink_muted)
                else
                    ColorProvider(R.color.widget_ink),
                fontSize = 13.sp,
                fontStyle = if (isDone) FontStyle.Italic else FontStyle.Normal,
            ),
            maxLines = 1,
        )
    }
}
