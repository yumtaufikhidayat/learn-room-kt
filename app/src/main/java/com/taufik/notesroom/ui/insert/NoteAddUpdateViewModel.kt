package com.taufik.notesroom.ui.insert

import android.app.Application
import androidx.lifecycle.ViewModel
import com.taufik.notesroom.database.Note
import com.taufik.notesroom.repository.NoteRepository

class NoteAddUpdateViewModel(application: Application) : ViewModel() {

    private val mNoteRepository = NoteRepository(application)

    fun insert(note: Note) = mNoteRepository.insert(note)

    fun update(note: Note) = mNoteRepository.update(note)

    fun delete(note: Note) = mNoteRepository.delete(note)
}