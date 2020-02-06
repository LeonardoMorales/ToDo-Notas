package com.android.quantum.notas.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.android.quantum.notas.R
import com.android.quantum.notas.models.Nota
import com.android.quantum.notas.persistance.NotaDatabase
import com.android.quantum.notas.ui.adapter.NoteRecyclerAdapter
import com.android.quantum.notas.utils.showToast
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity(), NoteRecyclerAdapter.Interaction {

    private val TAG: String = "AppDebug"
    private lateinit var noteAdapter: NoteRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()

        getSavedNotes()

        fab.setOnClickListener {
            addNewNote()
        }
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            noteAdapter = NoteRecyclerAdapter(interaction = this@MainActivity)
            adapter = noteAdapter

        }
        val itemTouchHelper = ItemTouchHelper(SwipeToDelete(noteAdapter, this@MainActivity))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun getSavedNotes() {
        launch {
            baseContext?.let {
                val notas = NotaDatabase(it).getNotaDao().mostrarNotas()
                val newList: ArrayList<Nota> = ArrayList(notas)
                withContext(Main){
                    noteAdapter.fillList(newList)
                }
            }
        }

    }

    private fun addNewNote() {
        val dialog = MaterialDialog(this)
            .noAutoDismiss()
            .customView(R.layout.layout_new_note)

        dialog.findViewById<TextView>(R.id.btnAceptar).setOnClickListener {

            val titulo: String = dialog.findViewById<TextInputEditText>(R.id.textInputEditTextTitle).text.toString()
            val nota: String = dialog.findViewById<TextInputEditText>(R.id.textInputEditTextNote).text.toString()

            val prioridad = dialog.getCustomView().findViewById<RadioButton>(
                dialog.findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId
            )

            prioridad?.let {
                dialog.findViewById<TextView>(R.id.tvRadioButtonsError).isVisible = false
                saveNote(titulo, nota, prioridad.text.toString())
                dialog.dismiss()
            } ?: kotlin.run {
                dialog.findViewById<TextView>(R.id.tvRadioButtonsError).isVisible = true
            }
        }

        dialog.findViewById<TextView>(R.id.btnCancelar).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveNote(titulo: String, nota: String, prioridad: String) {
        Log.d(TAG, "saveNote, saving note...")
        launch {
            val nota = Nota(titulo, nota, prioridad)
            baseContext?.let {
                NotaDatabase(it).getNotaDao().insertar(nota)
            }
        }
        getSavedNotes()
        this.showToast("Nueva nota creada")
    }

    private fun updateNote(nota: Nota) {
        Log.d(TAG, "updateNote, updating note...")
        launch {
            baseContext?.let {
                NotaDatabase(it).getNotaDao().actualizar(nota)
            }
        }
        getSavedNotes()
        this.showToast("La nota ha sido actualizada")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.nav_delete_notes -> {
                Log.d(TAG, "onOptionItemSelected, deleting all notes...")
                launch {
                    baseContext?.let {
                        NotaDatabase(it).getNotaDao().eliminarNotas()
                    }
                }
                noteAdapter.cleanList()
                this.showToast("Todas las notas han sido eliminadas")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemSelected(position: Int, nota: Nota) {
        val dialog = MaterialDialog(this)
            .noAutoDismiss()
            .customView(R.layout.layout_new_note)

        dialog.findViewById<TextInputEditText>(R.id.textInputEditTextTitle).setText(nota.titulo)

        dialog.findViewById<TextInputEditText>(R.id.textInputEditTextNote).setText(nota.descripcion)

        when(nota.prioridad) {
            "1" -> dialog.findViewById<RadioGroup>(R.id.radioGroup).check(R.id.radioBtn1)
            "2" -> dialog.findViewById<RadioGroup>(R.id.radioGroup).check(R.id.radioBtn2)
            "3" -> dialog.findViewById<RadioGroup>(R.id.radioGroup).check(R.id.radioBtn3)
        }


        dialog.findViewById<TextView>(R.id.btnAceptar).setOnClickListener {

            nota.titulo = dialog.findViewById<TextInputEditText>(R.id.textInputEditTextTitle).text.toString()
            nota.descripcion = dialog.findViewById<TextInputEditText>(R.id.textInputEditTextNote).text.toString()

            val prioridad = dialog.getCustomView().findViewById<RadioButton>(
                dialog.findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId
            )

            nota.prioridad = prioridad.text.toString()

            prioridad?.let {
                dialog.findViewById<TextView>(R.id.tvRadioButtonsError).isVisible = false
                updateNote(nota)
                dialog.dismiss()
            } ?: kotlin.run {
                dialog.findViewById<TextView>(R.id.tvRadioButtonsError).isVisible = true
            }
        }

        dialog.findViewById<TextView>(R.id.btnCancelar).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
