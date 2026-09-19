package com.pratikbhosale.daybook.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DataStore Preferences keys for all Settings fields defined in the TRD.
 *
 * Grouped and documented to match the TRD spec exactly. No extra keys.
 */
object SettingsKeys {

    /** "AUTO_CARRY" | "MANUAL" — use [RolloverMode.name] to encode */
    val ROLLOVER_MODE = stringPreferencesKey("rollover_mode")

    /** Show a confirmation step before the page turns; default true */
    val PAUSE_BEFORE_TURN = booleanPreferencesKey("pause_before_turn")

    /** Whether old archived pages are automatically deleted; default false */
    val ARCHIVE_AUTO_CLEAR = booleanPreferencesKey("archive_auto_clear")

    /**
     * How many days to retain archived pages before auto-delete.
     * -1 means "not set / unlimited" (used when [ARCHIVE_AUTO_CLEAR] is false).
     */
    val ARCHIVE_RETENTION_DAYS = intPreferencesKey("archive_retention_days")

    /** Minutes after a page seals before the widget fades to the next state */
    val WIDGET_FADE_DELAY_MINUTES = intPreferencesKey("widget_fade_delay_minutes")

    /** Key into the bundled font set, or a sentinel for the custom-font path */
    val TASK_FONT_KEY = stringPreferencesKey("task_font_key")

    /**
     * App-private URI to the user-uploaded font copy.
     * Empty string when no custom font is uploaded.
     * Never store the original SAF URI — only the copied private path.
     */
    val CUSTOM_FONT_URI = stringPreferencesKey("custom_font_uri")

    /**
     * Default reminder time encoded as "HH:mm" (24-hour).
     * Example: "09:00" for 9 AM.
     */
    val DEFAULT_REMINDER_TIME = stringPreferencesKey("default_reminder_time")
}
