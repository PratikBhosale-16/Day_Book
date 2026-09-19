package com.pratikbhosale.daybook.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pratikbhosale.daybook.data.model.Page
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDao {

    /** Active (non-archived) pages for a bucket, ordered by sortOrder. */
    @Query(
        "SELECT * FROM pages WHERE bucketId = :bucketId AND archivedAt IS NULL " +
            "ORDER BY sortOrder ASC"
    )
    fun observeActiveByBucket(bucketId: Long): Flow<List<Page>>

    /** Archived pages, newest-first. Use with paging — never load all. */
    @Query(
        "SELECT * FROM pages WHERE archivedAt IS NOT NULL " +
            "ORDER BY archivedAt DESC LIMIT :limit OFFSET :offset"
    )
    suspend fun getArchivedPage(limit: Int, offset: Int): List<Page>

    @Query("SELECT * FROM pages WHERE id = :id")
    suspend fun getById(id: Long): Page?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(page: Page): Long

    @Update
    suspend fun update(page: Page)

    @Query("DELETE FROM pages WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Seal a page: set sealedAt timestamp. Progress must still be derived, not stored. */
    @Query("UPDATE pages SET sealedAt = :sealedAt WHERE id = :id")
    suspend fun seal(id: Long, sealedAt: Long)

    /** Archive a page (midnight rollover or explicit user action). */
    @Query("UPDATE pages SET archivedAt = :archivedAt WHERE id = :id")
    suspend fun archive(id: Long, archivedAt: Long)
}
