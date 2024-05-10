package com.jhon.churivanti.tsecurity.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jhon.churivanti.tsecurity.R
import com.jhon.churivanti.tsecurity.data.network.Comentario

class CometarioAdapter : RecyclerView.Adapter<CometarioAdapter.ComentarioViewHolder>() {

    private var comentarios: List<Comentario> = emptyList()

    fun actualizarComentarios(nuevosComentarios: List<Comentario>) {
        comentarios = nuevosComentarios
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comentario, parent, false)
        return ComentarioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarios[position]
        holder.bind(comentario)
    }

    override fun getItemCount(): Int {
        return comentarios.size
    }

    class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(comentario: Comentario) {
            // Configura las vistas del itemView con los datos del comentario
            itemView.findViewById<TextView>(R.id.tvName).text = comentario.comentario

        }
    }
}