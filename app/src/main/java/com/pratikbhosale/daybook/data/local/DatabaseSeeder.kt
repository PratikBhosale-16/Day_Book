package com.pratikbhosale.daybook.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pratikbhosale.daybook.data.model.Bucket
import com.pratikbhosale.daybook.data.model.ColorKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Room [RoomDatabase.Callback] that seeds the three default buckets on first creation.
 *
 * Called from [DatabaseModule] via [Room.databaseBuilder().addCallback()].
 * The [coroutineScope] must outlive the database build — the application scope is used.
 */
class DatabaseSeeder(
    private val coroutineScope: CoroutineScope,
    private val bucketDaoProvider: () -> BucketDao,
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        coroutineScope.launch {
            seedDefaultBuckets(bucketDaoProvider())
        }
    }

    companion object {
        /**
         * Idempotent default bucket seed. Inserting with IGNORE conflict strategy means
         * running this twice is safe — the second run is a no-op.
         */
        suspend fun seedDefaultBuckets(dao: BucketDao) {
            dao.insertAll(defaultBuckets())
        }

        fun defaultBuckets(): List<Bucket> = listOf(
            Bucket(
                name = "Today",
                colorKey = ColorKey.TODAY,
                sortOrder = 0,
                isDefault = true,
            ),
            Bucket(
                name = "This week",
                colorKey = ColorKey.WEEK,
                sortOrder = 1,
                isDefault = true,
            ),
            Bucket(
                name = "Someday",
                colorKey = ColorKey.SOMEDAY,
                sortOrder = 2,
                isDefault = true,
            ),
        )
    }
}
