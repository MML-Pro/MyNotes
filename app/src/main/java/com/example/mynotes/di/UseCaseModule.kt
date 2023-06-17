package com.example.mynotes.di

import com.example.mynotes.domain.repo.NoteRepo
import com.example.mynotes.domain.usecase.AddNote
import com.example.mynotes.domain.usecase.DeleteNote
import com.example.mynotes.domain.usecase.GetAllNotes
import com.example.mynotes.domain.usecase.SearchNote
import com.example.mynotes.domain.usecase.UpdateNote
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideAddNoteUseCase(noteRepo: NoteRepo):AddNote{
        return AddNote(noteRepo)
    }

    @Provides
    fun provideUpdateNoteUseCase(noteRepo: NoteRepo):UpdateNote{
        return UpdateNote(noteRepo)
    }

    @Provides
    fun provideGetAllNotesUseCase(noteRepo: NoteRepo):GetAllNotes{
        return GetAllNotes(noteRepo)
    }

    @Provides
    fun provideSearchNoteUseCase(noteRepo: NoteRepo):SearchNote{
        return SearchNote(noteRepo)
    }

    @Provides
    fun provideDeleteNoteUseCase(noteRepo: NoteRepo):DeleteNote{
        return DeleteNote(noteRepo)
    }
}