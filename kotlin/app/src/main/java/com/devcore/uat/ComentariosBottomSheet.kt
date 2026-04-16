package com.devcore.uat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devcore.uat.adapters.ComentarioAdapter
import com.devcore.uat.data.SessionManager
import com.devcore.uat.network.ComentarioPublicacionCreate
import com.devcore.uat.network.ComentarioPublicacionResponse
import com.devcore.uat.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComentariosBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_PUB_ID = "pub_id"
        private const val ARG_COMENTARIOS = "comentarios"

        fun newInstance(
            pubId: Int,
            comentarios: ArrayList<ComentarioPublicacionResponse>
        ): ComentariosBottomSheet {
            return ComentariosBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PUB_ID, pubId)
                    // Convert to array of IDs/content for simplicity; pass via callback instead
                }
                this.pubId = pubId
                this.comentariosInitial = comentarios
            }
        }
    }

    var pubId: Int = -1
    var comentariosInitial: ArrayList<ComentarioPublicacionResponse> = arrayListOf()
    var onComentarioAdded: ((ComentarioPublicacionResponse) -> Unit)? = null
    var onComentarioDeleted: ((Int) -> Unit)? = null

    private lateinit var adapter: ComentarioAdapter
    private lateinit var sessionManager: SessionManager
    private var token = ""
    private var currentUserId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_comentarios, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        val rv = view.findViewById<RecyclerView>(R.id.rvComentarios)
        val etComentario = view.findViewById<EditText>(R.id.etComentario)
        val btnEnviar = view.findViewById<ImageView>(R.id.btnEnviarComentario)

        lifecycleScope.launch {
            token = sessionManager.authTokenFlow.firstOrNull() ?: ""
            currentUserId = sessionManager.userIdFlow.firstOrNull() ?: -1

            adapter = ComentarioAdapter(
                comentariosInitial.toMutableList(),
                currentUserId,
                onDeleteClick = { comentario, pos ->
                    borrarComentario(comentario, pos)
                }
            )
            rv.layoutManager = LinearLayoutManager(requireContext())
            rv.adapter = adapter

            // Scroll to bottom
            if (adapter.itemCount > 0) rv.scrollToPosition(adapter.itemCount - 1)
        }

        btnEnviar.setOnClickListener {
            val texto = etComentario.text.toString().trim()
            if (texto.isBlank()) {
                Toast.makeText(requireContext(), "Escribe algo primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            enviarComentario(texto, etComentario)
        }
    }

    private fun enviarComentario(texto: String, et: EditText) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.comentarPublicacion(
                    "Bearer $token",
                    pubId,
                    ComentarioPublicacionCreate(texto)
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val nuevo = response.body()!!
                        adapter.addComentario(nuevo)
                        et.text.clear()
                        onComentarioAdded?.invoke(nuevo)
                    } else {
                        Toast.makeText(requireContext(), "Error al comentar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun borrarComentario(comentario: ComentarioPublicacionResponse, pos: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.borrarComentario("Bearer $token", comentario.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        adapter.removeAt(pos)
                        onComentarioDeleted?.invoke(comentario.id)
                    } else {
                        Toast.makeText(requireContext(), "No se pudo borrar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
