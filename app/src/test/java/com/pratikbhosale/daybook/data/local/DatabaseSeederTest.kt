package com.pratikbhosale.daybook.data.local

import com.pratikbhosale.daybook.data.model.Bucket
import com.pratikbhosale.daybook.data.model.ColorKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for default bucket seeding logic in [DatabaseSeeder].
 *
 * The [FakeBucketDao] simulates the Room UNIQUE index on `name` + INSERT OR IGNORE:
 * inserts that conflict on name are silently skipped — exactly what the real constraint
 * enforces in production.
 */
class DatabaseSeederTest {

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * In-memory BucketDao that enforces name uniqueness, matching the UNIQUE index
     * on Bucket.name that backs INSERT OR IGNORE in production.
     */
    private class FakeBucketDao : BucketDao {
        val rows = mutableListOf<Bucket>()
        private var nextId = 1L

        override fun observeAll(): Flow<List<Bucket>> = flowOf(rows.toList())

        override suspend fun getById(id: Long): Bucket? = rows.find { it.id == id }

        override suspend fun insertAll(buckets: List<Bucket>) {
            for (bucket in buckets) {
                // Simulate INSERT OR IGNORE: skip if name already exists (unique index)
                if (rows.none { it.name == bucket.name }) {
                    rows.add(bucket.copy(id = nextId++))
                }
            }
        }

        override suspend fun insert(bucket: Bucket): Long {
            if (rows.none { it.name == bucket.name }) {
                val id = nextId++
                rows.add(bucket.copy(id = id))
                return id
            }
            return -1L // IGNORE
        }

        override suspend fun update(bucket: Bucket) {
            val idx = rows.indexOfFirst { it.id == bucket.id }
            if (idx >= 0) rows[idx] = bucket
        }

        override suspend fun deleteNonDefault(id: Long) {
            rows.removeAll { it.id == id && !it.isDefault }
        }

        override suspend fun count(): Int = rows.size
    }

    // ── Shape tests (single seed) ─────────────────────────────────────────────

    @Test
    fun defaultBuckets_returnsExactlyThreeBuckets() {
        val buckets = DatabaseSeeder.defaultBuckets()
        assertEquals("Expected exactly 3 default buckets", 3, buckets.size)
    }

    @Test
    fun defaultBuckets_allMarkedAsDefault() {
        val buckets = DatabaseSeeder.defaultBuckets()
        assertTrue("All seeded buckets must have isDefault = true", buckets.all { it.isDefault })
    }

    @Test
    fun defaultBuckets_hasToday_withCorrectColorAndOrder() {
        val today = DatabaseSeeder.defaultBuckets().find { it.name == "Today" }
        assertEquals(ColorKey.TODAY, today?.colorKey)
        assertEquals(0, today?.sortOrder)
    }

    @Test
    fun defaultBuckets_hasThisWeek_withCorrectColorAndOrder() {
        val week = DatabaseSeeder.defaultBuckets().find { it.name == "This week" }
        assertEquals(ColorKey.WEEK, week?.colorKey)
        assertEquals(1, week?.sortOrder)
    }

    @Test
    fun defaultBuckets_hasSomeday_withCorrectColorAndOrder() {
        val someday = DatabaseSeeder.defaultBuckets().find { it.name == "Someday" }
        assertEquals(ColorKey.SOMEDAY, someday?.colorKey)
        assertEquals(2, someday?.sortOrder)
    }

    // ── Idempotency tests (double seed) ───────────────────────────────────────

    @Test
    fun seedDefaultBuckets_calledTwice_rowCountStaysAtThree() = runTest {
        val dao = FakeBucketDao()

        DatabaseSeeder.seedDefaultBuckets(dao)
        DatabaseSeeder.seedDefaultBuckets(dao) // second call must be a no-op

        assertEquals(
            "Row count must be exactly 3 after seeding twice — INSERT OR IGNORE on name",
            3,
            dao.rows.size,
        )
    }

    @Test
    fun seedDefaultBuckets_calledTwice_noDuplicateNames() = runTest {
        val dao = FakeBucketDao()

        DatabaseSeeder.seedDefaultBuckets(dao)
        DatabaseSeeder.seedDefaultBuckets(dao)

        val names = dao.rows.map { it.name }
        assertEquals(
            "No duplicate bucket names after double seed",
            names.distinct().size,
            names.size,
        )
    }

    @Test
    fun seedDefaultBuckets_calledTwice_originalIdsPreserved() = runTest {
        val dao = FakeBucketDao()

        DatabaseSeeder.seedDefaultBuckets(dao)
        val idsAfterFirst = dao.rows.map { it.id }.toSet()

        DatabaseSeeder.seedDefaultBuckets(dao)
        val idsAfterSecond = dao.rows.map { it.id }.toSet()

        assertEquals(
            "Second seed must not create new rows — IDs must be identical",
            idsAfterFirst,
            idsAfterSecond,
        )
    }
}
