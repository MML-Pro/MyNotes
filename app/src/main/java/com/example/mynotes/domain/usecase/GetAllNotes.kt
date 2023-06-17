package com.example.mynotes.domain.usecase

import com.example.mynotes.domain.repo.NoteRepo

class GetAllNotes(private val noteRepo: NoteRepo) {

   suspend operator fun invoke() = noteRepo.getAllNotes()
}