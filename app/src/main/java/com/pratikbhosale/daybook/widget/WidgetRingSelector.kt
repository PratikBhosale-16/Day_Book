package com.pratikbhosale.daybook.widget

import com.pratikbhosale.daybook.R
import kotlin.math.roundToInt

/**
 * Maps a (done, total) pair to the correct ring drawable resource.
 *
 * Rules:
 *   - total == 0                → ic_ring_0_of_0 (empty ring, no segments)
 *   - done >= total && total>0  → ic_ring_complete (checkmark, all cases share one file)
 *   - total in [1..8]           → exact ic_ring_<done>_of_<total>
 *   - total > 8 (overflow)      → proportionally scale to nearest 8-segment equivalent.
 *     DESIGN: this preserves the segment aesthetic for unusual page sizes without crashing.
 *     The ring still reads as "some progress" or "nearly done" — it just cannot show
 *     finer granularity than 1/8 steps for pages beyond the generated drawable range.
 *
 * This is a pure function so it can be unit-tested without an Android context.
 */
object WidgetRingSelector {

    private const val MAX_TOTAL = 8

    fun ringDrawableRes(done: Int, total: Int): Int {
        if (total <= 0) return R.drawable.ic_ring_0_of_0
        if (done >= total) return R.drawable.ic_ring_complete

        val effectiveTotal: Int
        val effectiveDone: Int

        if (total <= MAX_TOTAL) {
            effectiveTotal = total
            effectiveDone = done
        } else {
            // Overflow: scale proportionally into 1..MAX_TOTAL range.
            effectiveTotal = MAX_TOTAL
            effectiveDone = ((done.toFloat() / total) * MAX_TOTAL)
                .roundToInt()
                .coerceIn(0, MAX_TOTAL - 1)
        }

        return ringResForExact(effectiveDone, effectiveTotal)
    }

    /**
     * Lookup table for exact (done, total) pairs in range [0..7, 1..8].
     * Generated to match the drawable filenames produced by generate_rings.ps1.
     */
    private fun ringResForExact(done: Int, total: Int): Int = when (total) {
        1 -> when (done) {
            0 -> R.drawable.ic_ring_0_of_1
            else -> R.drawable.ic_ring_complete
        }
        2 -> when (done) {
            0 -> R.drawable.ic_ring_0_of_2
            1 -> R.drawable.ic_ring_1_of_2
            else -> R.drawable.ic_ring_complete
        }
        3 -> when (done) {
            0 -> R.drawable.ic_ring_0_of_3
            1 -> R.drawable.ic_ring_1_of_3
            2 -> R.drawable.ic_ring_2_of_3
            else -> R.drawable.ic_ring_complete
        }
        4 -> when (done) {
            0 -> R.drawable.ic_ring_0_of_4
            1 -> R.drawable.ic_ring_1_of_4
            2 -> R.drawable.ic_ring_2_of_4
            3 -> R.drawable.ic_ring_3_of_4
            else -> R.drawable.ic_ring_complete
        }
        5 -> when (done) {
            0 -> R.drawable.ic_ring_0_of_5
            1 -> R.drawable.ic_ring_1_of_5
            2 -> R.drawable.ic_ring_2_of_5
            3 -> R.drawable.ic_ring_3_of_5
            4 -> R.drawable.ic_ring_4_of_5
            else -> R.drawable.ic_ring_complete
        }
        6 -> when (done) {
            0 -> R.drawable.ic_ring_0_of_6
            1 -> R.drawable.ic_ring_1_of_6
            2 -> R.drawable.ic_ring_2_of_6
            3 -> R.drawable.ic_ring_3_of_6
            4 -> R.drawable.ic_ring_4_of_6
            5 -> R.drawable.ic_ring_5_of_6
            else -> R.drawable.ic_ring_complete
        }
        7 -> when (done) {
            0 -> R.drawable.ic_ring_0_of_7
            1 -> R.drawable.ic_ring_1_of_7
            2 -> R.drawable.ic_ring_2_of_7
            3 -> R.drawable.ic_ring_3_of_7
            4 -> R.drawable.ic_ring_4_of_7
            5 -> R.drawable.ic_ring_5_of_7
            6 -> R.drawable.ic_ring_6_of_7
            else -> R.drawable.ic_ring_complete
        }
        8 -> when (done) {
            0 -> R.drawable.ic_ring_0_of_8
            1 -> R.drawable.ic_ring_1_of_8
            2 -> R.drawable.ic_ring_2_of_8
            3 -> R.drawable.ic_ring_3_of_8
            4 -> R.drawable.ic_ring_4_of_8
            5 -> R.drawable.ic_ring_5_of_8
            6 -> R.drawable.ic_ring_6_of_8
            7 -> R.drawable.ic_ring_7_of_8
            else -> R.drawable.ic_ring_complete
        }
        else -> R.drawable.ic_ring_0_of_0
    }
}
