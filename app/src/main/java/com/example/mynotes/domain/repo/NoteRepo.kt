package com.example.mynotes.domain.repo

import com.example.mynotes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepo {

    suspend fun insertNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun getAllNotes(): Flow<List<Note>>

    fun searchNote(query: String) : Flow<List<Note>>

    suspend fun deleteNote(note: Note)
}