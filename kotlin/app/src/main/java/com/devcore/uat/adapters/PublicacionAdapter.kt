package com.devcore.uat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devcore.uat.R
import com.devcore.uat.network.PublicacionComunidad
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.Date

class PublicacionAdapter(
    private var posts: List<PublicacionComunidad>,
    private val onApproveClick: ((PublicacionComunidad) -> Unit)? = null,
    private val onRejectClick: ((PublicacionComunidad) -> Unit)? = null,
    private val onUserClick: ((Int, String, String) -> Unit)? = null
) : RecyclerView.Adapter<PublicacionAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvAuthorName: TextView = view.findViewById(R.id.tvPostAuthorName)
        val tvStaffBadge: TextView = view.findViewById(R.id.tvStaffBadge)
        val tvPostDate: TextView = view.findViewById(R.id.tvPostDate)
        val tvPostContent: TextView = view.findViewById(R.id.tvPostContent)
        val tvBadgePending: TextView = view.findViewById(R.id.tvBadgePending)
        val tvBadgeOfficial: TextView = view.findViewById(R.id.tvBadgeOfficial)
        
        val layoutModerationButtons: View = view.findViewById(R.id.layoutModerationButtons)
        val btnApprove: View = view.findViewById(R.id.btnApprove)
        val btnReject: View = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_publicacion, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        
        holder.tvAuthorName.text = post.autor.nombre
        holder.tvPostContent.text = post.contenido
        
        val inicial = post.autor.nombre.firstOrNull()?.uppercase() ?: "U"
        holder.tvAvatar.text = inicial
        
        holder.tvPostDate.text = formatTimeAgo(post.fecha_creacion)
        
        // Mostrar badge de STAFF
        holder.tvStaffBadge.visibility = if (post.autor.es_staff == true) View.VISIBLE else View.GONE
        
        // Mostrar badge OFICIAL
        holder.tvBadgeOfficial.visibility = if (post.es_oficial == true) View.VISIBLE else View.GONE
        
        // Lógica para posts PENDIENTES
        if (post.estado == "PENDIENTE") {
            holder.tvBadgePending.visibility = View.VISIBLE
            // Si le pasamos los callbacks, asumimos que estamos en modo moderación
            if (onApproveClick != null && onRejectClick != null) {
                holder.layoutModerationButtons.visibility = View.VISIBLE
                
                holder.btnApprove.setOnClickListener { onApproveClick.invoke(post) }
                holder.btnReject.setOnClickListener { onRejectClick.invoke(post) }
            } else {
                holder.layoutModerationButtons.visibility = View.GONE
            }
        } else {
            holder.tvBadgePending.visibility = View.GONE
            holder.layoutModerationButtons.visibility = View.GONE
        }
        
        // Clic en avatar/nombre va a perfil (Opcional)
        val clickListener = View.OnClickListener {
            onUserClick?.invoke(post.autor.id, post.autor.nombre, post.autor.email)
        }
        holder.tvAvatar.setOnClickListener(clickListener)
        holder.tvAuthorName.setOnClickListener(clickListener)
    }

    override fun getItemCount() = posts.size

    fun submitList(newPosts: List<PublicacionComunidad>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    private fun formatTimeAgo(isoString: String): String {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            val past = format.parse(isoString) ?: return isoString
            
            val now = Date()
            val diffMs = now.time - past.time
            val diffHoras = diffMs / (1000 * 60 * 60)
            val diffMinutos = diffMs / (1000 * 60)
            
            return when {
                diffHoras > 24 -> {
                    val days = diffHoras / 24
                    "hace $days días"
                }
                diffHoras > 0 -> "hace $diffHoras h"
                diffMinutos > 0 -> "hace $diffMinutos min"
                else -> "justo ahora"
            }
        } catch (e: Exception) {
            return isoString
        }
    }
}
