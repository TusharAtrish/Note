package com.example.notes.model

import kotlinx.serialization.Serializable

@Serializable
data class NoteModel(
    var id: String? = null,
    var userId: String = "",
    var noteTitle: String = "",
    var noteDescription: String = "",
    var timeStamp: Long = 0
): java.io.Serializable

