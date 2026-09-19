package com.pratikbhosale.daybook.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pratikbhosale.daybook.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /** All tasks for a page, ordered for display. Used to derive progress. */
    @Query("SELECT * FROM tasks WHERE pageId = :pageId ORDER BY sortOrder ASC")
    fun observeByPage(pageId: Long): Flow<List<Task>>

    /** One-shot load for workers (rollover) that don't need streaming. */
    @Query("SELECT * FROM tasks WHERE pageId = :pageId ORDER BY sortOrder ASC")
    suspend fun getByPage(pageId: Long): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Mark a task done; clear isDone by passing isDone=false, doneAt=null. */
    @Query("UPDATE tasks SET isDone = :isDone, doneAt = :doneAt WHERE id = :id")
    suspend fun setDone(id: Long, isDone: Boolean, doneAt: Long?)

    /** Clear reminder fields when an alarm is cancelled or task is deleted. */
    @Query("UPDATE tasks SET reminderAt = NULL, reminderRequestCode = NULL WHERE id = :id")
    suspend fun clearReminder(id: Long)
}
