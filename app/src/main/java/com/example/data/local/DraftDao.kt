package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Draft
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {
    @Query("SELECT * FROM drafts ORDER BY timestamp DESC")
    fun getAllDrafts(): Flow<List<Draft>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: Draft): Long

    @Delete
    suspend fun deleteDraft(draft: Draft)

    @Query("DELETE FROM drafts WHERE id = :id")
    suspend fun deleteDraftById(id: Int)
}
