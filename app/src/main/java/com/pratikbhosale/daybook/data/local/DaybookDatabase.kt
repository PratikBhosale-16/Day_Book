package com.pratikbhosale.daybook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pratikbhosale.daybook.data.model.Bucket
import com.pratikbhosale.daybook.data.model.ColorKey
import com.pratikbhosale.daybook.data.model.Page
import com.pratikbhosale.daybook.data.model.Task

@Database(
    entities = [Bucket::class, Page::class, Task::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DaybookTypeConverters::class)
abstract class DaybookDatabase : RoomDatabase() {
    abstract fun bucketDao(): BucketDao
    abstract fun pageDao(): PageDao
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "daybook.db"
    }
}
