package com.duhapp.dnotes.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(noteEntity: NoteEntity): Long

    @Upsert
    suspend fun update(noteEntity: NoteEntity)

    @Query("SELECT * FROM NoteEntity WHERE category_id = :categoryId")
    suspend fun getNoteByCategoryId(categoryId: Int): List<NoteEntity>

    @Query("DELETE FROM NoteEntity WHERE id IN (:notes)")
    suspend fun deleteNotes(notes: List<Int>)

    @Query("SELECT * FROM NoteEntity WHERE id = :id")
    suspend fun getNoteById(id: Int): NoteEntity?

    @Update
    fun updateNotes(map: List<NoteEntity>)

    @Query("SELECT * FROM NoteEntity WHERE title LIKE '%' || :query || '%' OR details LIKE '%' || :query || '%' ORDER BY is_pinned DESC, updated_at DESC")
    suspend fun getNotesByQuery(query: String): List<NoteEntity>
}
