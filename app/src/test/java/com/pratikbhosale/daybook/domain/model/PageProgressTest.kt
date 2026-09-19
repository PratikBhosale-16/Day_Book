package com.pratikbhosale.daybook.domain.model

import com.pratikbhosale.daybook.data.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for [PageProgress] derivation logic.
 * Naming convention: `methodOrScenario_condition_expectedBehaviour`
 */
class PageProgressTest {

    private fun task(id: Long, isDone: Boolean) = Task(
        id = id,
        pageId = 1L,
        text = "task $id",
        isDone = isDone,
        sortOrder = id.toInt(),
    )

    @Test
    fun from_emptyList_zeroBothCounts() {
        val progress = PageProgress.from(emptyList())
        assertEquals(0, progress.total)
        assertEquals(0, progress.done)
    }

    @Test
    fun from_emptyList_isCompleteIsFalse() {
        val progress = PageProgress.from(emptyList())
        assertFalse("A page with no tasks should not be complete", progress.isComplete)
    }

    @Test
    fun from_allDone_isCompleteIsTrue() {
        val tasks = listOf(task(1, isDone = true), task(2, isDone = true))
        val progress = PageProgress.from(tasks)
        assertEquals(2, progress.total)
        assertEquals(2, progress.done)
        assertTrue(progress.isComplete)
    }

    @Test
    fun from_noneDone_isCompleteIsFalse() {
        val tasks = listOf(task(1, isDone = false), task(2, isDone = false))
        val progress = PageProgress.from(tasks)
        assertEquals(2, progress.total)
        assertEquals(0, progress.done)
        assertFalse(progress.isComplete)
    }

    @Test
    fun from_partiallyDone_countsCorrectly() {
        val tasks = listOf(
            task(1, isDone = true),
            task(2, isDone = false),
            task(3, isDone = true),
        )
        val progress = PageProgress.from(tasks)
        assertEquals(3, progress.total)
        assertEquals(2, progress.done)
        assertFalse(progress.isComplete)
    }

    @Test
    fun from_singleTaskDone_isComplete() {
        val progress = PageProgress.from(listOf(task(1, isDone = true)))
        assertTrue(progress.isComplete)
    }

    @Test
    fun from_singleTaskNotDone_notComplete() {
        val progress = PageProgress.from(listOf(task(1, isDone = false)))
        assertFalse(progress.isComplete)
    }
}
