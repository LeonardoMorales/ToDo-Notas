package com.android.quantum.notas.ui

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.quantum.notas.models.Nota
import com.android.quantum.notas.persistance.NotaDatabase
import com.android.quantum.notas.ui.adapter.NoteRecyclerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class SwipeToDelete(var adapter: NoteRecyclerAdapter, var context: Context): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        var pos = viewHolder.adapterPosition
        val nota: Nota = adapter.removeItem(pos)
        adapter.notifyDataSetChanged()
        CoroutineScope(IO).launch {
            context?.let {
                NotaDatabase(it).getNotaDao().eliminar(nota)
            }
        }

    }
}