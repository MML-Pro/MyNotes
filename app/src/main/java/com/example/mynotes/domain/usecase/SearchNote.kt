package com.example.mynotes.domain.usecase

import com.example.mynotes.domain.repo.NoteRepo

class SearchNote(private val noteRepo: NoteRepo) {

    operator fun invoke(query:String) = noteRepo.searchNote(query)
}