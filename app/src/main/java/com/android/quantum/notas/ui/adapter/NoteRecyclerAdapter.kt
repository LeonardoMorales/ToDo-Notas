package com.android.quantum.notas.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.quantum.notas.R
import com.android.quantum.notas.models.Nota
import kotlinx.android.synthetic.main.layout_note_item.view.*

class NoteRecyclerAdapter(private val interaction: Interaction? = null): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var listaNotas: ArrayList<Nota> = ArrayList<Nota>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_note_item, parent, false), interaction!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){

            is NoteViewHolder -> holder.bind(listaNotas[position])

        }
    }

    override fun getItemCount(): Int {
        return listaNotas.size
    }

    fun fillList(notas: ArrayList<Nota>){
        listaNotas = notas
        notifyDataSetChanged()
    }

    fun cleanList(){
        listaNotas = ArrayList<Nota>()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int): Nota {
        val notaEliminada = listaNotas[position]
        listaNotas.removeAt(position)
        return notaEliminada
    }

    class NoteViewHolder(
        itemView: View,
        private val interaction: Interaction
    ): RecyclerView.ViewHolder(itemView){

        private val titulo = itemView.tvTitulo
        private val prioridad = itemView.tvPrioridad
        private val descripcion = itemView.tvNota

        fun bind(nota: Nota) = with(itemView){

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, nota)
            }

            titulo.text = nota.titulo
            prioridad.text = nota.prioridad
            descripcion.text = nota.descripcion
        }

    }


    interface Interaction{
        fun onItemSelected(position: Int, nota: Nota)
    }
}