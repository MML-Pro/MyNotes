package com.example.mynotes.domain.usecase

import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.repo.NoteRepo

class UpdateNote(private val noteRepo: NoteRepo) {

    suspend operator fun invoke(note: Note) = noteRepo.updateNote(note)
}