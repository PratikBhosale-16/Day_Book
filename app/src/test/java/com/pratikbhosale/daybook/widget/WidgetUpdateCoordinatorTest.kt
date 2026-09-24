package com.pratikbhosale.daybook.widget

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [WidgetUpdateCoordinator] debounce behavior.
 *
 * Confirms that rapid successive [scheduleUpdate] calls within the debounce
 * window coalesce into a single [DaybookWidget.updateAll] invocation.
 *
 * Uses a fake coordinator with a tracked invocation count rather than the real
 * [DaybookWidget] (which requires a Context). The debounce logic under test is
 * entirely in the coordinator itself.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WidgetUpdateCoordinatorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private var updateCount = 0

    /**
     * Testable subclass: replaces [DaybookWidget().updateAll()] with a counter
     * increment so we can assert how many times the widget would have been refreshed.
     */
    private inner class FakeCoordinator : WidgetUpdateCoordinatorBase(testScope) {
        override fun doUpdate() {
            updateCount++
        }
    }

    private lateinit var coordinator: FakeCoordinator

    @Before
    fun setUp() {
        updateCount = 0
        coordinator = FakeCoordinator()
    }

    @Test
    fun singleCall_triggersOneUpdate() = testScope.runTest {
        coordinator.scheduleUpdate()
        advanceTimeBy(WidgetUpdateCoordinator.DEBOUNCE_MS + 1)
        assertEquals("Single call must produce exactly 1 update", 1, updateCount)
    }

    @Test
    fun threeRapidCalls_coalescesToOneUpdate() = testScope.runTest {
        coordinator.scheduleUpdate()
        advanceTimeBy(100)
        coordinator.scheduleUpdate()
        advanceTimeBy(100)
        coordinator.scheduleUpdate()
        advanceTimeBy(WidgetUpdateCoordinator.DEBOUNCE_MS + 1)
        assertEquals("Three rapid calls within debounce window must produce 1 update", 1, updateCount)
    }

    @Test
    fun twoCallsWithGapLargerThanDebounce_triggersTwoUpdates() = testScope.runTest {
        coordinator.scheduleUpdate()
        advanceTimeBy(WidgetUpdateCoordinator.DEBOUNCE_MS + 1)
        coordinator.scheduleUpdate()
        advanceTimeBy(WidgetUpdateCoordinator.DEBOUNCE_MS + 1)
        assertEquals("Two calls separated by debounce window must each produce 1 update", 2, updateCount)
    }

    @Test
    fun noCall_noUpdate() = testScope.runTest {
        advanceTimeBy(WidgetUpdateCoordinator.DEBOUNCE_MS * 2)
        assertEquals("No call must produce 0 updates", 0, updateCount)
    }
}
