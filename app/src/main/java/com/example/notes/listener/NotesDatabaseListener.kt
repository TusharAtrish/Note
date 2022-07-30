package com.example.notes.listener

import com.example.notes.model.NoteModel

interface NotesDatabaseListener {
    fun onNotesFetched(notesList: List<NoteModel>)
    fun onNoteSaved(noteModel: NoteModel)
    fun onNoteDeleted(noteModel: NoteModel)
    fun onError()
}