package com.devcore.uat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devcore.uat.R
import com.devcore.uat.network.ComentarioPublicacionResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ComentarioAdapter(
    private var comentarios: MutableList<ComentarioPublicacionResponse>,
    private val currentUserId: Int,
    private val onDeleteClick: ((ComentarioPublicacionResponse, Int) -> Unit)? = null
) : RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>() {

    class ComentarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatar: TextView = view.findViewById(R.id.tvCommentAvatar)
        val tvAuthor: TextView = view.findViewById(R.id.tvCommentAuthor)
        val tvContent: TextView = view.findViewById(R.id.tvCommentContent)
        val tvDate: TextView = view.findViewById(R.id.tvCommentDate)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comentario, parent, false)
        return ComentarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarios[position]
        holder.tvAvatar.text = comentario.autor.nombre.firstOrNull()?.uppercase() ?: "U"
        holder.tvAuthor.text = comentario.autor.nombre
        holder.tvContent.text = comentario.contenido
        holder.tvDate.text = formatTimeAgo(comentario.fecha_creacion)

        val isOwner = comentario.autor_id == currentUserId
        holder.btnDelete.visibility = if (isOwner) View.VISIBLE else View.GONE
        holder.btnDelete.setOnClickListener {
            onDeleteClick?.invoke(comentario, position)
        }
    }

    override fun getItemCount() = comentarios.size

    fun submitList(list: List<ComentarioPublicacionResponse>) {
        comentarios = list.toMutableList()
        notifyDataSetChanged()
    }

    fun addComentario(comentario: ComentarioPublicacionResponse) {
        comentarios.add(comentario)
        notifyItemInserted(comentarios.size - 1)
    }

    fun removeAt(position: Int) {
        comentarios.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun formatTimeAgo(isoString: String): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            val past = format.parse(isoString) ?: return isoString
            val diffMs = Date().time - past.time
            val diffMin = diffMs / (1000 * 60)
            val diffHoras = diffMin / 60
            when {
                diffHoras > 24 -> "hace ${diffHoras / 24} días"
                diffHoras > 0 -> "hace ${diffHoras} h"
                diffMin > 0 -> "hace ${diffMin} min"
                else -> "justo ahora"
            }
        } catch (e: Exception) {
            isoString
        }
    }
}
