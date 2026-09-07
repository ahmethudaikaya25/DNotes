package com.duhapp.dnotes.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(noteEntity: NoteEntity): Long

    @Upsert
    suspend fun update(noteEntity: NoteEntity)

    @Query("SELECT * FROM NoteEntity WHERE category_id = :categoryId")
    suspend fun getNoteByCategoryId(categoryId: Int): List<NoteEntity>

    @Query("SELECT id FROM NoteEntity WHERE category_id = :categoryId")
    suspend fun getNoteIdsByCategoryId(categoryId: Int): List<Int>

    @Query("UPDATE NoteEntity SET category_id = :targetCategoryId WHERE category_id = :sourceCategoryId")
    suspend fun moveNotesToCategory(sourceCategoryId: Int, targetCategoryId: Int)

    @Query("UPDATE NoteEntity SET category_id = :targetCategoryId WHERE id IN (:noteIds)")
    suspend fun moveNotesToCategoryByIds(noteIds: List<Int>, targetCategoryId: Int)

    @Query("DELETE FROM NoteEntity WHERE id IN (:notes)")
    suspend fun deleteNotes(notes: List<Int>)

    @Query("SELECT * FROM NoteEntity WHERE id = :id")
    suspend fun getNoteById(id: Int): NoteEntity?

    @Update
    fun updateNotes(map: List<NoteEntity>)

    @Query("SELECT * FROM NoteEntity WHERE title LIKE '%' || :query || '%' OR details LIKE '%' || :query || '%' ORDER BY is_pinned DESC, updated_at DESC")
    suspend fun getNotesByQuery(query: String): List<NoteEntity>

    @Query("SELECT * FROM NoteEntity")
    fun getAll(): Flow<List<NoteEntity>>
}
