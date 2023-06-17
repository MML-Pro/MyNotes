package com.example.mynotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.domain.model.Note
import com.example.mynotes.domain.usecase.AddNote
import com.example.mynotes.domain.usecase.DeleteNote
import com.example.mynotes.domain.usecase.GetAllNotes
import com.example.mynotes.domain.usecase.SearchNote
import com.example.mynotes.domain.usecase.UpdateNote
import com.example.mynotes.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(

    private val addNote: AddNote,
    private val updateNote: UpdateNote,
    private val getAllNotes: GetAllNotes,
    private val searchNote: SearchNote,
    private val deleteNote: DeleteNote

) : ViewModel() {

    private var _addNoteAsFlow = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val addNoteAsFlow: Flow<Resource<Boolean>> get() = _addNoteAsFlow

    private var _getAllNotesAsFlow = MutableStateFlow<Resource<List<Note>>>(Resource.Ideal())
    val getAllNotesAsFlow: Flow<Resource<List<Note>>> get() = _getAllNotesAsFlow

    private var _updateNoteAsFlow = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val updateNoteAsFlow: Flow<Resource<Boolean>> get() = _updateNoteAsFlow

    private var _searchNoteAsFlow = MutableStateFlow<Resource<List<Note>>>(Resource.Ideal())
    val searchNoteAsFlow: Flow<Resource<List<Note>>> get() = _searchNoteAsFlow

    private var _deleteNoteAsFlow = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val deleteNoteAsFlow: Flow<Resource<Boolean>> get() = _deleteNoteAsFlow

    fun addNote(note: Note) {
        _addNoteAsFlow.value = Resource.Loading()

        viewModelScope.launch {
            try {
                addNote.invoke(note)
                _addNoteAsFlow.emit(Resource.Success(true))
            } catch (ex: Exception) {
                _addNoteAsFlow.emit(Resource.Error(ex.message.toString()))
            }
        }
    }

    fun getAllNotes() {
        _getAllNotesAsFlow.value = Resource.Loading()
        viewModelScope.launch {

            try {
                getAllNotes.invoke().collect {
                    _getAllNotesAsFlow.emit(Resource.Success(it))
                }
            } catch (ex: Exception) {
                _getAllNotesAsFlow.emit(Resource.Error(ex.message.toString()))
            }
        }
    }

    fun editNote(note: Note){
        _updateNoteAsFlow.value = Resource.Loading()

        viewModelScope.launch {
            try {
                updateNote.invoke(note)
                _updateNoteAsFlow.emit(Resource.Success(true))
            } catch (ex: Exception) {
                _updateNoteAsFlow.emit(Resource.Error(ex.message.toString()))
            }
        }
    }

    fun searchNote(query:String){
        _searchNoteAsFlow.value = Resource.Loading()

        viewModelScope.launch {
            try {
                searchNote.invoke(query).collect{
                    _searchNoteAsFlow.emit(Resource.Success(it))
                }

            } catch (ex: Exception) {
                _searchNoteAsFlow.emit(Resource.Error(ex.message.toString()))
            }
        }
    }


    fun deleteNote(note: Note){
        _deleteNoteAsFlow.value = Resource.Loading()

        viewModelScope.launch {
            try {
                deleteNote.invoke(note)
                _deleteNoteAsFlow.emit(Resource.Success(true))
            } catch (ex: Exception) {
                _deleteNoteAsFlow.emit(Resource.Error(ex.message.toString()))
            }
        }
    }

}