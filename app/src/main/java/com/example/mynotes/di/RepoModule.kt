package com.example.mynotes.di

import com.example.mynotes.data.database.NoteDao
import com.example.mynotes.data.repo.NoteRepoImpl
import com.example.mynotes.domain.repo.NoteRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    fun provideRepo(noteDao: NoteDao):NoteRepo{
        return NoteRepoImpl(noteDao)
    }
}