package com.devcore.uat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devcore.uat.R
import com.devcore.uat.network.UsuarioResumen

class UserSearchAdapter(
    private var users: List<UsuarioResumen>,
    private val onUserClick: (UsuarioResumen) -> Unit
) : RecyclerView.Adapter<UserSearchAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val tvUserEmail: TextView = view.findViewById(R.id.tvUserEmail)
        val tvStaffBadge: TextView = view.findViewById(R.id.tvStaffBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario_search, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        
        holder.tvUserName.text = user.nombre
        holder.tvUserEmail.text = user.email
        
        val inicial = user.nombre.firstOrNull()?.uppercase() ?: "U"
        holder.tvAvatar.text = inicial
        
        if (user.es_staff == true) {
            holder.tvStaffBadge.visibility = View.VISIBLE
        } else {
            holder.tvStaffBadge.visibility = View.GONE
        }
        
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    override fun getItemCount() = users.size

    fun submitList(newUsers: List<UsuarioResumen>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
