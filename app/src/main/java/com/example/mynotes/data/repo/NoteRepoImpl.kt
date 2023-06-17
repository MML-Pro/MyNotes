package com.example.mynotes.data.repo

import com.example.mynotes.data.database.NoteDao
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.repo.NoteRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepoImpl @Inject constructor(private val noteDao: NoteDao) : NoteRepo {

    override suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    override suspend fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes()
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        return noteDao.searchNote(query)
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
}