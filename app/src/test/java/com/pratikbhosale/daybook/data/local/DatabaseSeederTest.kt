package com.pratikbhosale.daybook.data.local

import com.pratikbhosale.daybook.data.model.ColorKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for default bucket seeding logic in [DatabaseSeeder].
 *
 * These tests cover the shape and idempotency of the seed data without
 * requiring a database or a device.
 */
class DatabaseSeederTest {

    @Test
    fun defaultBuckets_returnsExactlyThreeBuckets() {
        val buckets = DatabaseSeeder.defaultBuckets()
        assertEquals("Expected exactly 3 default buckets", 3, buckets.size)
    }

    @Test
    fun defaultBuckets_allMarkedAsDefault() {
        val buckets = DatabaseSeeder.defaultBuckets()
        assertTrue(
            "All seeded buckets must have isDefault = true",
            buckets.all { it.isDefault }
        )
    }

    @Test
    fun defaultBuckets_hasToday() {
        val buckets = DatabaseSeeder.defaultBuckets()
        val today = buckets.find { it.name == "Today" }
        assertEquals("Today bucket must use TODAY color key", ColorKey.TODAY, today?.colorKey)
        assertEquals("Today bucket must have sortOrder 0", 0, today?.sortOrder)
    }

    @Test
    fun defaultBuckets_hasThisWeek() {
        val buckets = DatabaseSeeder.defaultBuckets()
        val week = buckets.find { it.name == "This week" }
        assertEquals("This week bucket must use WEEK color key", ColorKey.WEEK, week?.colorKey)
        assertEquals("This week bucket must have sortOrder 1", 1, week?.sortOrder)
    }

    @Test
    fun defaultBuckets_hasSomeday() {
        val buckets = DatabaseSeeder.defaultBuckets()
        val someday = buckets.find { it.name == "Someday" }
        assertEquals("Someday bucket must use SOMEDAY color key", ColorKey.SOMEDAY, someday?.colorKey)
        assertEquals("Someday bucket must have sortOrder 2", 2, someday?.sortOrder)
    }

    @Test
    fun defaultBuckets_sortOrdersAreUnique() {
        val buckets = DatabaseSeeder.defaultBuckets()
        val sortOrders = buckets.map { it.sortOrder }
        assertEquals("Sort orders must all be distinct", sortOrders.distinct().size, sortOrders.size)
    }

    @Test
    fun defaultBuckets_colorKeysAreUnique() {
        val buckets = DatabaseSeeder.defaultBuckets()
        val colorKeys = buckets.map { it.colorKey }
        assertEquals("Color keys must all be distinct", colorKeys.distinct().size, colorKeys.size)
    }
}
