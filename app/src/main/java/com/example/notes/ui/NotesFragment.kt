package com.example.notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.adapter.RVAdapter
import com.example.notes.databinding.FragmentNotesBinding
import com.example.notes.model.NoteModel
import com.example.notes.util.UiEvent
import com.example.notes.viewmodel.NotesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class NotesFragment : Fragment(), RVAdapter.NoteClickListener {

    private var binding: FragmentNotesBinding? = null
    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = RVAdapter(requireContext(), this)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding?.let {
            it.notercv.adapter = adapter
            it.notercv.layoutManager = layoutManager
            viewModel = ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[NotesViewModel:: class.java]

            viewModel.notesList.observe(viewLifecycleOwner) { notesList ->
                notesList?.let {
                    adapter.updateList(notesList)
                }
            }
            viewModel.getNotesList()
            it.addNoteButton.setOnClickListener {
                val action = NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(null)
                findNavController().navigate(action)
            }
        }
        collectUiEvents()
    }

    private fun collectUiEvents() {
        lifecycleScope.launch {
            viewModel.uiEvents.collectLatest { event ->
                when(event) {
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
        binding!!.notercv.visibility = if (!loading) View.VISIBLE else View.GONE
    }

    override fun onNoteClick(noteModel: NoteModel) {
        val action = NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(noteModel)
        findNavController().navigate(action)
    }

    override fun onDeleteIconClick(noteModel: NoteModel) {
        viewModel.deleteNote(noteModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
