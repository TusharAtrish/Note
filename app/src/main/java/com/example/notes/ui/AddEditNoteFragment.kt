package com.example.notes.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.R
import com.example.notes.databinding.FragmentAddEditNoteBinding
import com.example.notes.model.NoteModel
import com.example.notes.util.UiEvent
import com.example.notes.viewmodel.NotesViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddEditNoteFragment : Fragment() {

    private var binding: FragmentAddEditNoteBinding? = null

    private val args: AddEditNoteFragmentArgs by navArgs()

    private lateinit var noteModel: NoteModel

    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[NotesViewModel::class.java]
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteModel = args.noteModel ?: NoteModel(
            userId = FirebaseAuth.getInstance().uid.toString(),
            noteTitle = "",
            noteDescription = "",
            timeStamp = 0L
        )
        binding?.noteTitle?.setText(noteModel.noteTitle)
        binding?.notesDescription?.setText(noteModel.noteDescription)

        binding?.addNoteBtn?.setOnClickListener {
            saveNote()
        }
        collectUiEvents()
    }

    private fun collectUiEvents() {
        lifecycleScope.launch {
            viewModel.uiEvents.collectLatest { event ->
                when (event) {
                    is UiEvent.PopBackStack -> findNavController().popBackStack()
                    is UiEvent.ShowToast -> {
                        Toast.makeText(requireContext(), event.text, Toast.LENGTH_SHORT).show()
                    }
                    is UiEvent.ToggleLoading -> {
                        toggleLoading(event.loading)
                    }
                }
            }
        }
    }

    private fun toggleLoading(loading: Boolean) {
        binding!!.loading.visibility = if (loading) View.VISIBLE else View.GONE
        binding!!.addNoteBtn.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun saveNote() {
        noteModel.noteTitle = binding?.noteTitle?.text.toString()
        noteModel.noteDescription = binding?.notesDescription?.text.toString()

        viewModel.saveNote(noteModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}