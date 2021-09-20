package com.taufik.notesroom.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.taufik.notesroom.database.Note
import com.taufik.notesroom.databinding.ItemNoteBinding
import com.taufik.notesroom.helper.NoteDiffCallback

class NoteAdapter internal constructor(private val activity: Activity)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){

    private val listNotes = ArrayList<Note>()

    fun setListNotes(listNotes: List<Note>) {
        val diffCallback = NoteDiffCallback(this.listNotes, listNotes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listNotes.clear()
        this.listNotes.addAll(listNotes)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(listNotes[position])
    }

    override fun getItemCount(): Int = listNotes.size

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.apply {
                tvItemTitle.text = note.title
                tvItemDate.text = note.date
                tvItemDescription.text = note.description
                cvItemNote.setOnClickListener {
                    Intent(activity, NoteAddUpdateActivity::class.java).also {
                        it.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, adapterPosition)
                        it.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, note)
                        activity.startActivityForResult(it, NoteAddUpdateActivity.REQUEST_UPDATE)
                    }
                }
            }
        }
    }
}