package com.pratikbhosale.daybook.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Testable base class containing the debounce logic. Subclassed by the production
 * [WidgetUpdateCoordinator] (which calls [DaybookWidget.updateAll]) and by the
 * fake in [WidgetUpdateCoordinatorTest] (which just counts invocations).
 *
 * This separation lets tests exercise the coalescing behavior without an Android
 * context or a real Glance widget.
 */
abstract class WidgetUpdateCoordinatorBase(private val scope: CoroutineScope) {

    private var pendingJob: Job? = null

    /**
     * Schedule a widget refresh after [WidgetUpdateCoordinator.DEBOUNCE_MS].
     * Any existing pending refresh is cancelled and restarted — rapid successive
     * calls coalesce into a single update, per performance.md.
     */
    fun scheduleUpdate() {
        pendingJob?.cancel()
        pendingJob = scope.launch {
            delay(WidgetUpdateCoordinator.DEBOUNCE_MS)
            doUpdate()
        }
    }

    /** Called once per coalesced batch. Override to perform the actual update. */
    protected abstract fun doUpdate()
}

/**
 * Production coordinator: calls [DaybookWidget.updateAll] to push new state to
 * all active widget instances.
 *
 * Injected as a @Singleton into [DaybookRepositoryImpl]. Every repository write
 * calls [scheduleUpdate] — new write paths cannot accidentally omit it.
 */
@Singleton
class WidgetUpdateCoordinator @Inject constructor(
    @ApplicationContext private val context: Context,
    @com.pratikbhosale.daybook.data.di.ApplicationScope private val appScope: CoroutineScope,
) : WidgetUpdateCoordinatorBase(appScope) {

    override fun doUpdate() {
        // launch is fire-and-forget here; updateAll is already a suspend fun internally.
        appScope.launch {
            DaybookWidget().updateAll(context)
        }
    }

    companion object {
        /** Debounce window — long enough to coalesce rapid toggles, short enough to feel instant. */
        const val DEBOUNCE_MS = 400L
    }
}
