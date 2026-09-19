package com.pratikbhosale.daybook.domain.model

/** Rollover behaviour when a page is turned at midnight. */
enum class RolloverMode {
    /** Incomplete tasks are automatically carried to the new page. */
    AUTO_CARRY,

    /** User decides which tasks to carry manually. */
    MANUAL,
}
