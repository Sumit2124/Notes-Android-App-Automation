package com.example.myapp.ui.noteslist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.NoteRepository
import com.example.myapp.data.local.NoteDatabase
import com.example.myapp.data.model.Note
import com.example.myapp.data.model.NoteType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Primary constructor for production, secondary for testing
class NotesListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository = NoteRepository(NoteDatabase.getDatabase(application).noteDao())


    private val _filterType = MutableStateFlow<NoteType?>(null)
    private val _isSortedByTitle = MutableStateFlow(false)
    
    val allNotes: LiveData<List<Note>> = repository.allNotes.asLiveData()

    fun deleteNote(note: Note) {
        viewModelScope.launch { repository.deleteNote(note) }
    }
    
    fun filterByType(noteType: NoteType?) {
        _filterType.value = noteType
        updateFilteredNotes()
    }
    
    fun toggleSortByTitle() {
        _isSortedByTitle.value = !_isSortedByTitle.value
        updateFilteredNotes()
    }
    
    private fun updateFilteredNotes() {
        viewModelScope.launch {
            repository.allNotes
                .map { notes ->
                    var filteredNotes = notes
                    
                    // Apply type filter
                    _filterType.value?.let { type ->
                        filteredNotes = filteredNotes.filter { it.noteType == type }
                    }
                    
                    // Apply sorting
                    if (_isSortedByTitle.value) {
                        filteredNotes = filteredNotes.sortedBy { it.name }
                    }
                    
                    filteredNotes
                }
                .collect { filteredNotes ->
                    // Update the LiveData with filtered and sorted notes
                    (allNotes as MutableLiveData).value = filteredNotes
                }
        }
    }
}
