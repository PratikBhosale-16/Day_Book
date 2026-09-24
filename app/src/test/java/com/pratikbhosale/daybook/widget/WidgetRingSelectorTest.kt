package com.pratikbhosale.daybook.widget

import com.pratikbhosale.daybook.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Unit tests for [WidgetRingSelector.ringDrawableRes].
 *
 * Verifies: empty state, complete state, exact in-range pairs, overflow scaling,
 * and boundary/edge cases. Pure function — no Android context needed.
 */
class WidgetRingSelectorTest {

    // ── Empty / no tasks ─────────────────────────────────────────────────────

    @Test
    fun total0_returnsEmptyRing() {
        assertEquals(R.drawable.ic_ring_0_of_0, WidgetRingSelector.ringDrawableRes(0, 0))
    }

    @Test
    fun negativeTotal_treatedAsEmpty() {
        assertEquals(R.drawable.ic_ring_0_of_0, WidgetRingSelector.ringDrawableRes(0, -1))
    }

    // ── Complete ─────────────────────────────────────────────────────────────

    @Test
    fun doneEqualsTotal_returnsComplete() {
        assertEquals(R.drawable.ic_ring_complete, WidgetRingSelector.ringDrawableRes(1, 1))
        assertEquals(R.drawable.ic_ring_complete, WidgetRingSelector.ringDrawableRes(3, 3))
        assertEquals(R.drawable.ic_ring_complete, WidgetRingSelector.ringDrawableRes(8, 8))
    }

    @Test
    fun doneExceedsTotal_treatedAsComplete() {
        assertEquals(R.drawable.ic_ring_complete, WidgetRingSelector.ringDrawableRes(5, 3))
    }

    // ── Exact in-range pairs ──────────────────────────────────────────────────

    @Test
    fun ring_0_of_1_isDistinctFromEmpty() {
        val ring = WidgetRingSelector.ringDrawableRes(0, 1)
        assertNotEquals(R.drawable.ic_ring_0_of_0, ring)
        assertEquals(R.drawable.ic_ring_0_of_1, ring)
    }

    @Test
    fun ring_1_of_3_isCorrect() {
        assertEquals(R.drawable.ic_ring_1_of_3, WidgetRingSelector.ringDrawableRes(1, 3))
    }

    @Test
    fun ring_3_of_4_isCorrect() {
        assertEquals(R.drawable.ic_ring_3_of_4, WidgetRingSelector.ringDrawableRes(3, 4))
    }

    @Test
    fun ring_7_of_8_isCorrect() {
        assertEquals(R.drawable.ic_ring_7_of_8, WidgetRingSelector.ringDrawableRes(7, 8))
    }

    // ── Overflow (total > 8) ──────────────────────────────────────────────────

    @Test
    fun overflow_0doneof10_scalesTo0of8() {
        // 0/10 → 0% → effectiveDone=0, effectiveTotal=8
        assertEquals(R.drawable.ic_ring_0_of_8, WidgetRingSelector.ringDrawableRes(0, 10))
    }

    @Test
    fun overflow_5doneof10_showsSomeFill() {
        // 5/10 = 50% → effectiveDone ≈ 4, effectiveTotal=8
        val result = WidgetRingSelector.ringDrawableRes(5, 10)
        assertNotEquals(R.drawable.ic_ring_0_of_0, result)
        assertNotEquals(R.drawable.ic_ring_complete, result)
    }

    @Test
    fun overflow_complete_returnsComplete() {
        // 10/10 = done>=total → complete regardless of overflow
        assertEquals(R.drawable.ic_ring_complete, WidgetRingSelector.ringDrawableRes(10, 10))
    }

    @Test
    fun overflow_almostDone_doesNotReturnComplete() {
        // 9/10 should scale to something partial, not complete
        val result = WidgetRingSelector.ringDrawableRes(9, 10)
        assertNotEquals(R.drawable.ic_ring_complete, result)
    }

    // ── Consistency: all in-range pairs return distinct resources ─────────────

    @Test
    fun allExactPairs_haveUniqueOrSharedResIds() {
        // For a given total, done=0..total-1 should each produce different resource IDs.
        for (total in 1..8) {
            val seen = mutableSetOf<Int>()
            for (done in 0 until total) {
                val res = WidgetRingSelector.ringDrawableRes(done, total)
                assert(seen.add(res)) {
                    "Duplicate drawable for done=$done total=$total: $res already seen"
                }
            }
        }
    }
}
