package com.example.notes.util

sealed class UiEvent {
    data class ShowToast(val text: String): UiEvent()
    data class ToggleLoading(val loading: Boolean): UiEvent()
    object PopBackStack: UiEvent()
}
