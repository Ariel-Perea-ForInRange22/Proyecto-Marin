package com.devcore.uat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devcore.uat.R
import com.devcore.uat.network.PublicacionComunidad
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PublicacionAdapter(
    private var posts: List<PublicacionComunidad>,
    private val onApproveClick: ((PublicacionComunidad) -> Unit)? = null,
    private val onRejectClick: ((PublicacionComunidad) -> Unit)? = null,
    private val onUserClick: ((Int, String, String) -> Unit)? = null,
    private val onLikeClick: ((PublicacionComunidad, Int) -> Unit)? = null,
    private val onCommentClick: ((PublicacionComunidad) -> Unit)? = null,
    private val onDeleteClick: ((PublicacionComunidad, Int) -> Unit)? = null,
    private val currentUserId: Int = -1
) : RecyclerView.Adapter<PublicacionAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvAuthorName: TextView = view.findViewById(R.id.tvPostAuthorName)
        val tvPostCommunity: TextView? = view.findViewById(R.id.tvPostCommunity)
        val tvStaffBadge: TextView = view.findViewById(R.id.tvStaffBadge)
        val tvPostDate: TextView = view.findViewById(R.id.tvPostDate)
        val tvPostContent: TextView = view.findViewById(R.id.tvPostContent)
        val tvBadgePending: TextView = view.findViewById(R.id.tvBadgePending)
        val tvBadgeOfficial: TextView = view.findViewById(R.id.tvBadgeOfficial)
        val tvLikesCountLabel: TextView? = view.findViewById(R.id.tvLikesCountLabel)
        val tvCommentsCountLabel: TextView? = view.findViewById(R.id.tvCommentsCountLabel)
        val btnLike: TextView? = view.findViewById(R.id.btnLike)
        val btnComment: TextView? = view.findViewById(R.id.btnComment)
        val btnOptions: ImageView? = view.findViewById(R.id.btnOptions)
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
        holder.tvAvatar.text = post.autor.nombre.firstOrNull()?.uppercase() ?: "U"
        holder.tvPostDate.text = formatTimeAgo(post.fecha_creacion)

        // Comunidad name for global feed
        if (!post.comunidad_nombre.isNullOrBlank()) {
            holder.tvPostCommunity?.text = "en ${post.comunidad_nombre}"
            holder.tvPostCommunity?.visibility = View.VISIBLE
        } else {
            holder.tvPostCommunity?.visibility = View.GONE
        }

        // STAFF badge
        holder.tvStaffBadge.visibility = if (post.autor.es_staff == true) View.VISIBLE else View.GONE

        // OFICIAL badge
        holder.tvBadgeOfficial.visibility = if (post.es_oficial == true) View.VISIBLE else View.GONE

        // PENDIENTE badge + moderation
        if (post.estado == "PENDIENTE") {
            holder.tvBadgePending.visibility = View.VISIBLE
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

        // Likes counter
        val likesCount = post.likes_count
        holder.tvLikesCountLabel?.text = if (likesCount == 1) "1 me gusta" else "$likesCount me gusta"

        // Comments counter
        val commentsCount = post.comentarios.size
        holder.tvCommentsCountLabel?.text = if (commentsCount == 1) "1 comentario" else "$commentsCount comentarios"

        // Like button state
        val yaReacciono = post.usuario_ha_reaccionado == true
        holder.btnLike?.apply {
            text = if (yaReacciono) "👍 Me gusta" else "👍 Me gusta"
            setTextColor(
                if (yaReacciono)
                    context.getColor(R.color.uat_azul)
                else
                    context.getColor(R.color.text_secondary)
            )
            setOnClickListener {
                onLikeClick?.invoke(post, position)
            }
        }

        // Comment button
        holder.btnComment?.setOnClickListener {
            onCommentClick?.invoke(post)
        }

        // Avatar / name click → profile
        val profileClick = View.OnClickListener {
            onUserClick?.invoke(post.autor.id, post.autor.nombre, post.autor.email)
        }
        holder.tvAvatar.setOnClickListener(profileClick)
        holder.tvAuthorName.setOnClickListener(profileClick)

        // 3-dots menu
        holder.btnOptions?.setOnClickListener { anchor ->
            val popup = PopupMenu(anchor.context, anchor)
            val isOwner = post.autor_id == currentUserId
            if (isOwner) {
                popup.menu.add(0, 1, 0, "Borrar publicación")
            }
            popup.menu.add(0, 2, 1, "Reportar publicación")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> onDeleteClick?.invoke(post, position)
                    2 -> android.widget.Toast.makeText(anchor.context, "Publicación reportada", android.widget.Toast.LENGTH_SHORT).show()
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount() = posts.size

    fun submitList(newPosts: List<PublicacionComunidad>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    fun updatePostAt(position: Int, updated: PublicacionComunidad) {
        val mutable = posts.toMutableList()
        mutable[position] = updated
        posts = mutable
        notifyItemChanged(position)
    }

    fun removePostAt(position: Int) {
        val mutable = posts.toMutableList()
        mutable.removeAt(position)
        posts = mutable
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
