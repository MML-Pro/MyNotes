package com.example.mynotes.di

import android.content.Context
import com.example.mynotes.data.database.NoteDao
import com.example.mynotes.data.database.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) : NoteDatabase{
        return NoteDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideDao(noteDatabase: NoteDatabase) : NoteDao{
        return noteDatabase.noteDao
    }

}