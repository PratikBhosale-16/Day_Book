package com.pratikbhosale.daybook.domain.model

/**
 * Typed representation of the user's settings. Read from DataStore and mapped here;
 * never backed by Room.
 *
 * Defaults match the TRD spec:
 *   rolloverMode = AUTO_CARRY
 *   pauseBeforeTurn = true
 *   archiveAutoClear = false
 *   archiveRetentionDays = null (unlimited)
 *   widgetFadeDelayMinutes = 5
 *   taskFontKey = "default"
 *   customFontUri = null
 *   defaultReminderTime = "09:00"
 */
data class Settings(
    val rolloverMode: RolloverMode = RolloverMode.AUTO_CARRY,
    val pauseBeforeTurn: Boolean = true,
    val archiveAutoClear: Boolean = false,
    val archiveRetentionDays: Int? = null,
    val widgetFadeDelayMinutes: Int = 5,
    val taskFontKey: String = "default",
    val customFontUri: String? = null,
    val defaultReminderTime: String = "09:00",
)
