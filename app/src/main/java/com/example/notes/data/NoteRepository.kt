package com.example.notes.data

import com.example.notes.listener.NotesDatabaseListener
import com.example.notes.model.NoteModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class NoteRepository(private val listener: NotesDatabaseListener) {
    private val database = FirebaseFirestore.getInstance()

    suspend fun getAllNotes(userId: String) {
        database.collection(NOTES).whereEqualTo(USER_ID, userId).get().addOnSuccessListener {
            listener.onNotesFetched(it.toObjects(NoteModel::class.java))
        }.addOnFailureListener {
            listener.onError()
        }
    }

    suspend fun saveNote(noteModel: NoteModel) {
        val doc = if (noteModel.id == null) database.collection(NOTES).document()
        else database.collection(NOTES).document(noteModel.id.toString())
        noteModel.id = doc.id
        noteModel.timeStamp = Calendar.getInstance().timeInMillis
        doc.set(noteModel).addOnSuccessListener {
            listener.onNoteSaved(noteModel)
        }.addOnFailureListener {
            listener.onError()
        }
    }

    suspend fun delete(noteModel: NoteModel) {
        database.collection(NOTES).document(noteModel.id.toString()).delete().addOnSuccessListener {
            listener.onNoteDeleted(noteModel)
        }.addOnFailureListener {
            listener.onError()
        }
    }

    companion object {
        const val NOTES = "notes"
        const val USER_ID = "userId"
    }
}