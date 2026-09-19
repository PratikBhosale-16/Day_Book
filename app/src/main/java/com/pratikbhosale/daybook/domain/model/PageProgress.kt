package com.pratikbhosale.daybook.domain.model

import com.pratikbhosale.daybook.data.model.Task

/**
 * Page progress derived from its tasks. Never stored — computed on demand.
 *
 * Per TRD: "Derived, never stored: a page's progress (count of done tasks / total).
 * Computing it is cheap; storing it creates a second source of truth that will drift."
 *
 * A page with zero tasks has no meaningful progress and is not considered sealed.
 */
data class PageProgress(
    val total: Int,
    val done: Int,
) {
    /**
     * True when every task is done and there is at least one task.
     * A page with no tasks cannot be sealed by progress alone.
     */
    val isComplete: Boolean get() = total > 0 && done == total

    companion object {
        fun from(tasks: List<Task>): PageProgress = PageProgress(
            total = tasks.size,
            done = tasks.count { it.isDone },
        )
    }
}
