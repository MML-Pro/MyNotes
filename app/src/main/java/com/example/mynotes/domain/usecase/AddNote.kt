package com.example.mynotes.domain.usecase

import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.repo.NoteRepo

class AddNote(private val noteRepo: NoteRepo) {

    suspend fun invoke(note:Note) = noteRepo.insertNote(note)
}