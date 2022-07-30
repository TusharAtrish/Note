package com.example.notes.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.NoteModel
import com.example.notes.util.Util

class RVAdapter(
    private val context: Context,
    private val noteClickListener : NoteClickListener

) : RecyclerView.Adapter<RVAdapter.NotesViewHolder>() {

    private var allNotes = listOf<NoteModel>()

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title : TextView = itemView.findViewById(R.id.itemTitle)
        val timestamp : TextView = itemView.findViewById(R.id.itemTimeStamp)
        val delete : ImageView = itemView.findViewById(R.id.itemDeleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item,parent,false)
        return NotesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNote = allNotes[position]
        holder.title.text = currentNote.noteTitle
        holder.timestamp.text = Util.getFormattedTime(currentNote.timeStamp)
        holder.delete.setOnClickListener {
            noteClickListener.onDeleteIconClick(currentNote)
        }
        holder.itemView.setOnClickListener {
            noteClickListener.onNoteClick(currentNote)
        }
    }

    override fun getItemCount(): Int {
        return allNotes.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList : List<NoteModel>){
        allNotes = newList
        notifyDataSetChanged()
    }
    interface NoteClickListener{
        fun onNoteClick(noteModel: NoteModel)
        fun onDeleteIconClick(noteModel: NoteModel)

    }
}