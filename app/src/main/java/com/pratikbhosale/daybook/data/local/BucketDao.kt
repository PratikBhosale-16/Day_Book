package com.pratikbhosale.daybook.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pratikbhosale.daybook.data.model.Bucket
import kotlinx.coroutines.flow.Flow

@Dao
interface BucketDao {

    @Query("SELECT * FROM buckets ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<Bucket>>

    @Query("SELECT * FROM buckets WHERE id = :id")
    suspend fun getById(id: Long): Bucket?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(buckets: List<Bucket>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bucket: Bucket): Long

    @Update
    suspend fun update(bucket: Bucket)

    @Query("DELETE FROM buckets WHERE id = :id AND isDefault = 0")
    suspend fun deleteNonDefault(id: Long)

    @Query("SELECT COUNT(*) FROM buckets")
    suspend fun count(): Int
}
