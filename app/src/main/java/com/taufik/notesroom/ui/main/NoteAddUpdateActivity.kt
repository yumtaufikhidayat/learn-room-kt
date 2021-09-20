package com.taufik.notesroom.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.taufik.notesroom.R
import com.taufik.notesroom.database.Note
import com.taufik.notesroom.databinding.ActivityNoteAddUpdateBinding
import com.taufik.notesroom.helper.DataHelper
import com.taufik.notesroom.helper.ViewModelFactory
import com.taufik.notesroom.ui.insert.NoteAddUpdateViewModel

class NoteAddUpdateActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    private var _binding: ActivityNoteAddUpdateBinding? = null
    private val binding get() = _binding
    private lateinit var noteAddUpdateViewModel: NoteAddUpdateViewModel

    private var isEdit = false
    private var note: Note? = null
    private var position = 0
    private var actionBarTitle: String = ""
    private var btnTitle: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNoteAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setViewModel()

        setNote()

        setEdit()

        initActionBar()

        submitData()

        submitDataOnClick()
    }

    private fun setViewModel() {
        noteAddUpdateViewModel = obtainViewModel(this)
    }

    private fun setNote() {
        note = intent.getParcelableExtra(EXTRA_NOTE)
        if (note != null) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            isEdit = true
        } else {
            note = Note()
        }
    }

    private fun setEdit() {

        binding?.apply {
            if (isEdit) {
                actionBarTitle = getString(R.string.change)
                btnTitle = getString(R.string.update)

                if (note != null) {
                    note?.let {
                        edtTitle.setText(it.title)
                        edtDescription.setText(it.description)
                    }
                }
            } else {
                actionBarTitle = getString(R.string.add)
                btnTitle = getString(R.string.save)
            }
        }
    }

    private fun initActionBar() {
        supportActionBar?.apply {
            title = actionBarTitle
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun submitData(){
        binding?.apply {
            btnSubmit.text = btnTitle
        }
    }

    private fun submitDataOnClick() {

        binding?.apply {
            btnSubmit.setOnClickListener {
                val title = edtTitle.text.toString().trim()
                val description = edtDescription.text.toString().trim()

                if (title.isEmpty()) {
                    edtTitle.error = getString(R.string.empty)
                } else if (description.isEmpty()) {
                    edtDescription.error = getString(R.string.empty)
                } else {
                    note.let {
                        it?.title = title
                        it?.description = description
                    }

                    val intent = Intent().apply {
                        putExtra(EXTRA_NOTE, note)
                        putExtra(EXTRA_POSITION, position)
                    }

                    if (isEdit) {
                        noteAddUpdateViewModel.update(note as Note)
                        setResult(RESULT_UPDATE, intent)
                        finish()
                    } else {
                        note.let {
                            it?.date = DataHelper.getCurrentDate()
                        }

                        noteAddUpdateViewModel.insert(note as Note)
                        setResult(RESULT_ADD, intent)
                        finish()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(type: Int) {

        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = getString(R.string.cancel)
            dialogMessage = getString(R.string.message_cancel)
        } else {
            dialogMessage = getString(R.string.message_delete)
            dialogTitle = getString(R.string.delete)
        }

        AlertDialog.Builder(this).apply {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (!isDialogClose) {

                    noteAddUpdateViewModel.delete(note as Note)

                    val intent = Intent()
                    intent.putExtra(EXTRA_POSITION, position)
                    setResult(RESULT_DELETE, intent)
                }
                finish()
            }
        }.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun obtainViewModel(activity: AppCompatActivity): NoteAddUpdateViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(NoteAddUpdateViewModel::class.java)
    }
}