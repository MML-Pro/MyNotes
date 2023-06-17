package com.example.mynotes.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mynotes.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM notes_table ORDER BY id DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes_table WHERE title LIKE '%' || :query || '%' OR note_content LIKE '%' || :query || '%' OR date_time LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchNote(query: String): Flow<List<Note>>

    @Delete
    suspend fun deleteNote(note: Note)


}