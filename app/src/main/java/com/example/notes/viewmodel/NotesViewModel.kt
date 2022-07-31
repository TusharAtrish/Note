package com.example.notes.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NoteRepository
import com.example.notes.listener.NotesDatabaseListener
import com.example.notes.model.NoteModel
import com.example.notes.util.UiEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application),
    NotesDatabaseListener {

    private val _notesList = MutableLiveData<List<NoteModel>>(null)
    val notesList: LiveData<List<NoteModel>> = _notesList

    private val repository = NoteRepository(this)

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun getNotesList() {
        sendEvent(UiEvent.ToggleLoading(true))
        viewModelScope.launch {
            repository.getAllNotes(
                FirebaseAuth.getInstance().currentUser?.uid.toString()
            )
        }
    }

    fun saveNote(noteModel: NoteModel){
        sendEvent(UiEvent.ToggleLoading(true))
        viewModelScope.launch{
            repository.saveNote(noteModel)
        }

    }

    fun deleteNote(noteModel: NoteModel){
        viewModelScope.launch {
            repository.delete( noteModel)
        }
    }


    override fun onNotesFetched(notesList: List<NoteModel>) {
        _notesList.postValue(notesList.sortedByDescending {
            it.timeStamp
        })
        sendEvent(UiEvent.ToggleLoading(false))
    }

    override fun onNoteSaved(noteModel: NoteModel) {
        sendEvent(UiEvent.ToggleLoading(false))
        sendEvent(UiEvent.PopBackStack)
    }

    override fun onNoteDeleted(noteModel: NoteModel) {

        _notesList.postValue(
            _notesList.value?.filterNot {
                it.id == noteModel.id
            }
        )
        sendEvent(UiEvent.ShowToast("Deleted"))
    }

    override fun onError() {

    }

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvents.send(event)
        }
    }


}