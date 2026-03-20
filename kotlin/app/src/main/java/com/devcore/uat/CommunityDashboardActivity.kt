package com.devcore.uat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.devcore.uat.data.SessionManager
import com.devcore.uat.databinding.ActivityCommunityDashboardBinding
import com.devcore.uat.network.PublicacionRequest
import com.devcore.uat.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommunityDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityDashboardBinding
    private lateinit var sessionManager: SessionManager

    private var token = ""
    private var communityId = 1
    private var isAnonymous = false
    private var feelingEmoji = ""
    private var activeTab = 0
    private var currentUserName = "Tú"
    private var attachedImageUri: Uri? = null

    // ─── DATA ────────────────────────────────────────────────────────────────

    data class Post(
        val id: Int,
        val autor: String,
        val rol: String,
        val contenido: String,
        val tiempo: String,
        var likes: Int = 0,
        var comentarios: MutableList<String> = mutableListOf(),
        val foto: Uri? = null
    )

    data class Evento(val titulo: String, val fecha: String, val lugar: String, val tipo: String)

    data class Archivo(val nombre: String, val tipo: String, val tamaño: String, val fecha: String)

    private val allPosts = mutableListOf(
        Post(1, "Administrador UAT", "Administrador", "🎉 Felicitamos a los alumnos que participaron en el hackathon regional. ¡Orgullo UAT!", "Hace 1 día", likes = 24),
        Post(2, "María García", "Administrador", "📣 ¡Bienvenidos al nuevo semestre! Revisen los horarios en el portal UAT.", "Hace 2 días", likes = 15, comentarios = mutableListOf("Gracias!", "¿Ya cambiaron los horarios?")),
        Post(3, "Carlos Hernández", "Miembro", "¿Alguien tiene apuntes del parcial de Algoritmos? Se me perdieron 😅", "Hace 3 días", likes = 8),
        Post(4, "Ana López", "Miembro", "🔔 La entrega del proyecto final de POO es el viernes a las 11:59pm. Suban su repo en GitHub.", "Hace 4 días", likes = 32, comentarios = mutableListOf("Gracias!", "¿Es individual o en equipo?"))
    )

    private val eventos = listOf(
        Evento("Hackathon UAT 2024", "15 Mar 2024 · 9:00am", "Campus Principal", "pasado"),
        Evento("Conferencia IA y Futuro", "22 Mar 2024 · 10:00am", "Auditorio Central", "pasado"),
        Evento("Semana de Exámenes", "20–25 Abr 2025 · Todo el día", "Campus", "presente"),
        Evento("Feria de Proyectos Finales", "5 May 2025 · 9:00am", "Edificio C", "futuro"),
        Evento("Congreso de Tecnología UAT", "15 Jun 2025 · 8:00am", "Centro de Convenciones", "futuro"),
        Evento("Hackathon Regional 2025", "20 Jul 2025 · 9:00am", "Campus TIC", "futuro")
    )

    private val archivos = listOf(
        Archivo("Apuntes_Algoritmos_U1.pdf", "PDF", "2.4 MB", "Hace 3 días"),
        Archivo("Proyecto_POO_v2.zip", "ZIP", "8.1 MB", "Hace 5 días"),
        Archivo("Presentacion_IA.pptx", "PPTX", "3.7 MB", "Hace 1 sem"),
        Archivo("DB_Normalizacion.xlsx", "XLSX", "512 KB", "Hace 2 sem"),
        Archivo("Cheatsheet_Git.pdf", "PDF", "340 KB", "Hace 3 sem")
    )

    private val sentimientos = listOf("😊 Feliz", "😢 Triste", "😡 Enojado", "😍 Emocionado", "😴 Cansado", "😅 Nervioso", "🤩 Orgulloso", "😎 Genial")

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            attachedImageUri = uri
            binding.btnAttachImage.text = "✅"
            Toast.makeText(this, "Imagen adjuntada", Toast.LENGTH_SHORT).show()
        }
    }

    // ─── LIFECYCLE ───────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        communityId = intent.getIntExtra("COMMUNITY_ID", 1)
        binding.tvComunidadNombre.text = intent.getStringExtra("COMMUNITY_NAME") ?: "Comunidad UAT"
        binding.tvMiembros.text = "${intent.getIntExtra("MEMBER_COUNT", 128)} miembros"

        lifecycleScope.launch {
            token = sessionManager.authTokenFlow.firstOrNull() ?: ""
            loadCurrentUser()
            setupClickListeners()
            setupTabs()
            renderPostsFeed(allPosts)
        }
    }

    private suspend fun loadCurrentUser() {
        if (token.isEmpty()) return
        try {
            val resp = withContext(Dispatchers.IO) { RetrofitClient.apiService.getMe("Bearer $token") }
            if (resp.isSuccessful) currentUserName = resp.body()?.nombre ?: "Tú"
        } catch (_: Exception) {}
    }

    // ─── TABS ────────────────────────────────────────────────────────────────

    private fun setupTabs() {
        val tabs = listOf(binding.tabDestacados, binding.tabTu, binding.tabFotos, binding.tabEventos, binding.tabArchivo)

        fun selectTab(index: Int) {
            tabs.forEach { it.setTextColor(ContextCompat.getColor(this, R.color.text_secondary)); it.setBackgroundResource(0) }
            tabs[index].setTextColor(ContextCompat.getColor(this, R.color.uat_azul))
            tabs[index].setBackgroundResource(R.drawable.tab_selected_indicator)
            activeTab = index
        }

        selectTab(0)

        binding.tabDestacados.setOnClickListener { selectTab(0); showComposer(true); renderPostsFeed(allPosts) }

        binding.tabTu.setOnClickListener {
            selectTab(1)
            showComposer(false)
            renderMisPublicaciones()
        }

        binding.tabFotos.setOnClickListener {
            selectTab(2)
            showComposer(false)
            renderFotos()
        }

        binding.tabEventos.setOnClickListener {
            selectTab(3)
            showComposer(false)
            renderEventos()
        }

        binding.tabArchivo.setOnClickListener {
            selectTab(4)
            showComposer(false)
            renderArchivos()
        }
    }

    private fun showComposer(visible: Boolean) {
        // The composer is the 4th child of the main LinearLayout
        val composerIndex = 4 // 0=cover, 1=info, 2=tabs, 3=divider, 4=composer
        val root = binding.feedContainer.parent as? LinearLayout ?: return
        val composer = root.getChildAt(composerIndex)
        composer?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    // ─── MAIN CLICK LISTENERS ────────────────────────────────────────────────

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // 🔍 SEARCH
        binding.btnSearch.setOnClickListener { showSearchDialog() }

        // ⋯ THREE DOTS MENU
        binding.btnMore.setOnClickListener { v -> showMoreMenu(v) }

        // ✅ MIEMBRO menu
        binding.btnMiembro.setOnClickListener { v -> showMiembroMenu(v) }

        // 👥 INVITAR
        binding.btnInvitar.setOnClickListener {
            val name = binding.tvComunidadNombre.text.toString()
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "¡Únete a \"$name\" en la app UAT! 🎓")
            }, "Invitar a través de..."))
        }

        // 🔒 ANÓNIMO toggle
        binding.btnPublicacionAnonima.setOnClickListener {
            isAnonymous = !isAnonymous
            binding.btnPublicacionAnonima.text = if (isAnonymous) "🔒 Anónimo ✓" else "🔒 Anónimo"
            val color = if (isAnonymous) R.color.uat_azul else R.color.text_secondary
            binding.btnPublicacionAnonima.setTextColor(ContextCompat.getColor(this, color))
            Toast.makeText(this, if (isAnonymous) "Publicación anónima activada" else "Publicación anónima desactivada", Toast.LENGTH_SHORT).show()
        }

        // 😊 SENTIMIENTO
        binding.btnSentimiento.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("¿Cómo te sientes?")
                .setItems(sentimientos.toTypedArray()) { _, i ->
                    feelingEmoji = sentimientos[i].split(" ").first()
                    binding.btnSentimiento.text = sentimientos[i]
                    binding.btnSentimiento.setTextColor(ContextCompat.getColor(this, R.color.uat_azul))
                }
                .setNegativeButton("Quitar") { _, _ ->
                    feelingEmoji = ""
                    binding.btnSentimiento.text = "😊 Sentimiento"
                    binding.btnSentimiento.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
                }
                .show()
        }

        // 🖼️ ATTACH IMAGE/FILE
        binding.btnAttachImage.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Adjuntar")
                .setItems(arrayOf("📷 Foto o imagen", "📄 Archivo o documento")) { _, which ->
                    if (which == 0) imagePickerLauncher.launch("image/*")
                    else imagePickerLauncher.launch("*/*")
                }
                .show()
        }

        // ✉️ PUBLICAR
        binding.btnPublicar.setOnClickListener {
            val texto = binding.etPost.text.toString().trim()
            if (texto.isEmpty() && attachedImageUri == null) {
                Toast.makeText(this, "Escribe algo o adjunta una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            publicarPost(texto)
        }

        // ⚙️ FILTRAR
        binding.btnFiltrar.setOnClickListener { v -> showFiltrarMenu(v) }
    }

    // ─── SEARCH ──────────────────────────────────────────────────────────────

    private fun showSearchDialog() {
        val et = com.google.android.material.textfield.TextInputEditText(this)
        et.hint = "Buscar publicaciones, usuarios..."
        et.setPadding(40, 20, 40, 20)

        val dialog = AlertDialog.Builder(this)
            .setTitle("🔍 Buscar en la comunidad")
            .setView(et)
            .setPositiveButton("Buscar") { _, _ ->
                val query = et.text.toString().trim()
                if (query.isEmpty()) { renderPostsFeed(allPosts); return@setPositiveButton }
                val results = allPosts.filter {
                    it.contenido.contains(query, true) || it.autor.contains(query, true)
                }
                binding.tvFeedLabel.text = "Resultados para \"$query\" (${results.size})"
                renderPostsFeed(results)
            }
            .setNegativeButton("Cancelar") { _, _ -> renderPostsFeed(allPosts); binding.tvFeedLabel.text = "Más relevantes" }
            .create()

        // Listen for live text changes to show instant results
        et.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s.toString().trim()
                if (q.isEmpty()) return
                val res = allPosts.filter { it.contenido.contains(q, true) || it.autor.contains(q, true) }
                binding.tvFeedLabel.text = if (q.isEmpty()) "Más relevantes" else "${res.size} resultado(s)"
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        dialog.show()
    }

    // ─── THREE DOTS MENU ─────────────────────────────────────────────────────

    private fun showMoreMenu(anchor: View) {
        val opciones = arrayOf(
            "📋 Administrar tu contenido",
            "🔔 Administrar notificaciones",
            "🚫 Dejar de seguir grupo",
            "👤 Perfil de miembro",
            "📌 Desfijar grupo",
            "↗️ Compartir grupo",
            "🚩 Reportar grupo"
        )
        AlertDialog.Builder(this)
            .setTitle(binding.tvComunidadNombre.text)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarMisPublicacionesDialog()
                    1 -> Toast.makeText(this, "Notificaciones del grupo configuradas", Toast.LENGTH_SHORT).show()
                    2 -> confirmarDejarDeSeguir()
                    3 -> Toast.makeText(this, "Tu ID de miembro: ${currentUserName}", Toast.LENGTH_SHORT).show()
                    4 -> Toast.makeText(this, "Grupo desfijado de tu feed", Toast.LENGTH_SHORT).show()
                    5 -> {
                        val name = binding.tvComunidadNombre.text.toString()
                        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"; putExtra(Intent.EXTRA_TEXT, "Únete a \"$name\" en la app UAT 🎓")
                        }, "Compartir grupo..."))
                    }
                    6 -> confirmarReportarGrupo()
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    // ─── MIEMBRO MENU ────────────────────────────────────────────────────────

    private fun showMiembroMenu(anchor: View) {
        val opciones = arrayOf(
            "🚫 Dejar de seguir el grupo",
            "🔔 Administrar notificaciones",
            "🚪 Abandonar grupo"
        )
        AlertDialog.Builder(this)
            .setTitle("Opciones de membresía")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> confirmarDejarDeSeguir()
                    1 -> Toast.makeText(this, "Notificaciones del grupo ajustadas", Toast.LENGTH_SHORT).show()
                    2 -> confirmarAbandonarGrupo()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ─── FILTER ──────────────────────────────────────────────────────────────

    private fun showFiltrarMenu(anchor: View) {
        val opciones = arrayOf("⭐ Más relevantes", "⏱️ Actividad reciente", "🆕 Publicaciones nuevas")
        AlertDialog.Builder(this)
            .setTitle("Ordenar publicaciones")
            .setItems(opciones) { _, which ->
                val sorted = when (which) {
                    0 -> allPosts.sortedByDescending { it.likes }
                    1 -> allPosts.sortedByDescending { it.comentarios.size }
                    2 -> allPosts.toList() // already newest-first by insertion order
                    else -> allPosts.toList()
                }
                binding.tvFeedLabel.text = opciones[which]
                renderPostsFeed(sorted)
                Toast.makeText(this, "Ordenando por: ${opciones[which]}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ─── PUBLICAR ────────────────────────────────────────────────────────────

    private fun publicarPost(texto: String) {
        val contenido = buildString {
            if (feelingEmoji.isNotEmpty()) append("$feelingEmoji ")
            append(texto)
        }
        val autor = if (isAnonymous) "Publicación anónima" else currentUserName
        val newPost = Post(
            id = allPosts.size + 1,
            autor = autor,
            rol = if (isAnonymous) "Anónimo" else "Miembro",
            contenido = contenido,
            tiempo = "Ahora mismo",
            foto = attachedImageUri
        )
        allPosts.add(0, newPost)

        // Reset composer
        binding.etPost.setText("")
        attachedImageUri = null
        isAnonymous = false
        feelingEmoji = ""
        binding.btnAttachImage.text = "🖼️"
        binding.btnPublicacionAnonima.text = "🔒 Anónimo"
        binding.btnPublicacionAnonima.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        binding.btnSentimiento.text = "😊 Sentimiento"
        binding.btnSentimiento.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))

        renderPostsFeed(allPosts)
        Toast.makeText(this, "✅ Publicación enviada (pendiente de revisión)", Toast.LENGTH_SHORT).show()

        if (token.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                try { RetrofitClient.apiService.crearPublicacion("Bearer $token", communityId, PublicacionRequest(contenido)) }
                catch (_: Exception) {}
            }
        }
    }

    // ─── TAB RENDERERS ───────────────────────────────────────────────────────

    private fun renderPostsFeed(posts: List<Post>) {
        binding.feedContainer.removeAllViews()
        if (posts.isEmpty()) {
            addEmptyState("No hay publicaciones aquí todavía")
            return
        }
        posts.forEach { agregarPostView(it) }
    }

    private fun renderMisPublicaciones() {
        binding.feedContainer.removeAllViews()
        val myPosts = allPosts.filter { it.autor == currentUserName || it.autor == "Tú" }
        binding.tvFeedLabel.text = "Tus publicaciones (${myPosts.size})"
        if (myPosts.isEmpty()) { addEmptyState("Aún no has publicado nada en esta comunidad"); return }
        myPosts.forEach { agregarPostView(it) }
    }

    private fun renderFotos() {
        binding.feedContainer.removeAllViews()
        binding.tvFeedLabel.text = "📷 Fotos de la comunidad"
        val postsConFoto = allPosts.filter { it.foto != null }

        val grid = GridLayout(this).apply {
            columnCount = 3
            layoutParams = LinearLayout.LayoutParams(-1, -2)
        }

        if (postsConFoto.isEmpty()) {
            // Show placeholder photos
            val fotoEmojis = listOf("🖼️","📸","🎨","🖼️","📷","🎭","🌅","📸","🎪")
            fotoEmojis.forEachIndexed { i, emoji ->
                val cell = TextView(this@CommunityDashboardActivity).apply {
                    text = emoji
                    textSize = 36f
                    gravity = android.view.Gravity.CENTER
                    setPadding(8, 8, 8, 8)
                    val size = (resources.displayMetrics.widthPixels - 48) / 3
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = size; height = size
                        setMargins(4, 4, 4, 4)
                    }
                    setBackgroundColor(ContextCompat.getColor(this@CommunityDashboardActivity, R.color.uat_gris_claro))
                    setOnClickListener { Toast.makeText(this@CommunityDashboardActivity, "Ver publicación $i", Toast.LENGTH_SHORT).show() }
                }
                grid.addView(cell)
            }
        } else {
            postsConFoto.forEachIndexed { i, post ->
                val cell = TextView(this@CommunityDashboardActivity).apply {
                    text = "🖼️"
                    textSize = 36f
                    gravity = android.view.Gravity.CENTER
                    val size = (resources.displayMetrics.widthPixels - 48) / 3
                    layoutParams = GridLayout.LayoutParams().apply { width = size; height = size; setMargins(4,4,4,4) }
                    setOnClickListener { renderPostsFeed(listOf(post)) }
                }
                grid.addView(cell)
            }
        }
        binding.feedContainer.addView(grid)
    }

    private fun renderEventos() {
        binding.feedContainer.removeAllViews()
        binding.tvFeedLabel.text = "📅 Eventos de la comunidad"

        listOf("pasado", "presente", "futuro").forEach { tipo ->
            val eventosDeTipo = eventos.filter { it.tipo == tipo }
            if (eventosDeTipo.isEmpty()) return@forEach

            val header = TextView(this).apply {
                text = when(tipo) { "pasado" -> "📁 Eventos pasados"; "presente" -> "⏰ Ahora"; else -> "📌 Próximos eventos" }
                textSize = 14f; setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(16, 20, 16, 8)
                setTextColor(ContextCompat.getColor(this@CommunityDashboardActivity, R.color.uat_negro))
            }
            binding.feedContainer.addView(header)

            eventosDeTipo.forEach { evento ->
                val card = LayoutInflater.from(this).inflate(R.layout.item_community_post, null)
                card.findViewById<TextView>(R.id.tvPostAutor).text = evento.titulo
                card.findViewById<TextView>(R.id.tvPostRol).text = when(tipo) { "futuro" -> "Próximo"; "presente" -> "Hoy"; else -> "Pasado" }
                card.findViewById<TextView>(R.id.tvPostContenido).text = "📅 ${evento.fecha}\n📍 ${evento.lugar}"
                card.findViewById<TextView>(R.id.tvPostTime).text = ""
                val likeCount = card.findViewById<TextView>(R.id.tvLikeCount)
                val commentCount = card.findViewById<TextView>(R.id.tvCommentCount)
                likeCount.visibility = View.GONE
                commentCount.visibility = View.GONE

                val btnLike = card.findViewById<TextView>(R.id.btnLike)
                val btnComment = card.findViewById<TextView>(R.id.btnComment)

                if (tipo == "futuro") {
                    btnLike.text = "⭐ Me interesa"
                    var interesado = false
                    btnLike.setOnClickListener {
                        interesado = !interesado
                        btnLike.text = if (interesado) "⭐ ¡Me interesa!" else "⭐ Me interesa"
                        btnLike.setTextColor(ContextCompat.getColor(this, if (interesado) R.color.uat_azul else R.color.text_secondary))
                    }
                } else {
                    btnLike.visibility = View.GONE
                }
                btnComment.text = "💬 Comentar"
                btnComment.setOnClickListener { Toast.makeText(this, "Próximamente: comentarios de eventos", Toast.LENGTH_SHORT).show() }

                card.findViewById<LinearLayout>(R.id.commentsSection).visibility = View.GONE
                card.findViewById<TextView>(R.id.btnPostMenu).setOnClickListener { Toast.makeText(this, "Opciones del evento", Toast.LENGTH_SHORT).show() }

                binding.feedContainer.addView(card)
            }
        }
    }

    private fun renderArchivos() {
        binding.feedContainer.removeAllViews()
        binding.tvFeedLabel.text = "📁 Archivos de la comunidad"

        archivos.forEach { archivo ->
            val row = LayoutInflater.from(this).inflate(R.layout.item_community_post, null)
            val iconos = mapOf("PDF" to "📄", "ZIP" to "🗜️", "PPTX" to "📊", "XLSX" to "📈", "DOC" to "📝")
            row.findViewById<TextView>(R.id.tvPostAutor).text = archivo.nombre
            row.findViewById<TextView>(R.id.tvPostRol).text = archivo.tipo
            row.findViewById<TextView>(R.id.tvPostContenido).text = "${iconos[archivo.tipo] ?: "📎"} ${archivo.tamaño} · Subido ${archivo.fecha}"
            row.findViewById<TextView>(R.id.tvPostTime).text = ""
            row.findViewById<TextView>(R.id.tvLikeCount).visibility = View.GONE
            row.findViewById<TextView>(R.id.tvCommentCount).visibility = View.GONE
            row.findViewById<LinearLayout>(R.id.commentsSection).visibility = View.GONE

            val btnLike = row.findViewById<TextView>(R.id.btnLike)
            val btnComment = row.findViewById<TextView>(R.id.btnComment)
            btnLike.text = "⬇️ Descargar"
            btnLike.setOnClickListener { Toast.makeText(this, "Descargando ${archivo.nombre}...", Toast.LENGTH_SHORT).show() }
            btnComment.text = "🔗 Copiar enlace"
            btnComment.setOnClickListener { Toast.makeText(this, "Enlace copiado al portapapeles", Toast.LENGTH_SHORT).show() }

            row.findViewById<TextView>(R.id.btnPostMenu).setOnClickListener {
                Toast.makeText(this, "Opciones del archivo", Toast.LENGTH_SHORT).show()
            }
            binding.feedContainer.addView(row)
        }
    }

    // ─── POST CARD ───────────────────────────────────────────────────────────

    private fun agregarPostView(post: Post) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_community_post, null)

        val tvAutor = view.findViewById<TextView>(R.id.tvPostAutor)
        val tvRol = view.findViewById<TextView>(R.id.tvPostRol)
        val tvContenido = view.findViewById<TextView>(R.id.tvPostContenido)
        val tvTime = view.findViewById<TextView>(R.id.tvPostTime)
        val tvLikeCount = view.findViewById<TextView>(R.id.tvLikeCount)
        val tvCommentCount = view.findViewById<TextView>(R.id.tvCommentCount)
        val btnLike = view.findViewById<TextView>(R.id.btnLike)
        val btnComment = view.findViewById<TextView>(R.id.btnComment)
        val commentsSection = view.findViewById<LinearLayout>(R.id.commentsSection)
        val commentsList = view.findViewById<LinearLayout>(R.id.commentsList)
        val etComentario = view.findViewById<TextInputEditText>(R.id.etComentario)
        val btnSendComment = view.findViewById<TextView>(R.id.btnSendComment)
        val btnMenu = view.findViewById<TextView>(R.id.btnPostMenu)

        tvAutor.text = post.autor
        tvRol.text = post.rol
        tvContenido.text = post.contenido
        tvTime.text = "${post.tiempo} · 🌐"
        tvLikeCount.text = if (post.likes > 0) "👍 ${post.likes}" else ""
        tvCommentCount.text = if (post.comentarios.isNotEmpty()) "${post.comentarios.size} comentarios" else ""
        post.comentarios.forEach { agregarComentarioView(commentsList, it) }
        if (post.comentarios.isNotEmpty()) commentsSection.visibility = View.VISIBLE

        // Like toggle
        var liked = false
        btnLike.setOnClickListener {
            liked = !liked
            post.likes = if (liked) post.likes + 1 else maxOf(0, post.likes - 1)
            btnLike.text = if (liked) "👍 Me gusta" else "🤍 Me gusta"
            btnLike.setTextColor(ContextCompat.getColor(this, if (liked) R.color.uat_azul else R.color.text_secondary))
            tvLikeCount.text = if (post.likes > 0) "👍 ${post.likes}" else ""
        }

        // Comentar - expand/collapse
        btnComment.setOnClickListener {
            val show = commentsSection.visibility != View.VISIBLE
            commentsSection.visibility = if (show) View.VISIBLE else View.GONE
            if (show) etComentario.requestFocus()
        }
        tvCommentCount.setOnClickListener { commentsSection.visibility = View.VISIBLE }

        // Send comment
        btnSendComment.setOnClickListener {
            val texto = etComentario.text.toString().trim()
            if (texto.isEmpty()) return@setOnClickListener
            post.comentarios.add(texto)
            agregarComentarioView(commentsList, texto)
            etComentario.setText("")
            tvCommentCount.text = "${post.comentarios.size} comentarios"
        }

        // ⋯ Post context menu
        btnMenu.setOnClickListener { v ->
            val popup = PopupMenu(this, v)
            popup.menu.add(0, 1, 0, "📌 Guardar publicación")
            popup.menu.add(0, 2, 0, "🚩 Reportar")
            popup.menu.add(0, 3, 0, "🔔 Seguir publicación")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> Toast.makeText(this, "Guardado en tu archivo personal", Toast.LENGTH_SHORT).show()
                    2 -> Toast.makeText(this, "Publicación reportada. La revisaremos pronto.", Toast.LENGTH_SHORT).show()
                    3 -> Toast.makeText(this, "Recibirás notificaciones de esta publicación", Toast.LENGTH_SHORT).show()
                }; true
            }
            popup.show()
        }

        binding.feedContainer.addView(view)
    }

    private fun agregarComentarioView(container: LinearLayout, texto: String) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 4, 0, 4) }
        val avatar = TextView(this).apply { text = "👤"; textSize = 14f; setPadding(0, 0, 8, 0) }
        val bg = android.graphics.drawable.GradientDrawable().apply {
            setColor(resources.getColor(R.color.uat_gris_claro, null))
            cornerRadius = 16f * resources.displayMetrics.density
        }
        val body = TextView(this).apply {
            text = texto; textSize = 13f; setPadding(12, 6, 12, 6)
            setTextColor(resources.getColor(R.color.uat_negro, null)); background = bg
        }
        row.addView(avatar); row.addView(body)
        container.addView(row)
    }

    private fun addEmptyState(message: String) {
        val tv = TextView(this).apply {
            text = "😔\n\n$message"
            textSize = 15f; gravity = android.view.Gravity.CENTER; setPadding(32, 64, 32, 64)
            setTextColor(ContextCompat.getColor(this@CommunityDashboardActivity, R.color.text_secondary))
        }
        binding.feedContainer.addView(tv)
    }

    // ─── DIALOGS ─────────────────────────────────────────────────────────────

    private fun mostrarMisPublicacionesDialog() {
        val myPosts = allPosts.filter { it.autor == currentUserName || it.autor == "Tú" }
        val items = if (myPosts.isEmpty()) arrayOf("No tienes publicaciones aún") else myPosts.map { it.contenido.take(60) + "..." }.toTypedArray()
        AlertDialog.Builder(this).setTitle("📋 Tu contenido").setItems(items) { _, i ->
            if (myPosts.isNotEmpty()) {
                binding.tabTu.performClick()
            }
        }.setNegativeButton("Cerrar", null).show()
    }

    private fun confirmarDejarDeSeguir() {
        AlertDialog.Builder(this)
            .setTitle("Dejar de seguir el grupo")
            .setMessage("Ya no verás contenido de \"${binding.tvComunidadNombre.text}\" en tu feed. Seguirás siendo miembro.")
            .setPositiveButton("Dejar de seguir") { _, _ -> Toast.makeText(this, "Dejaste de seguir el grupo", Toast.LENGTH_SHORT).show() }
            .setNegativeButton("Cancelar", null).show()
    }

    private fun confirmarAbandonarGrupo() {
        AlertDialog.Builder(this)
            .setTitle("Abandonar grupo")
            .setMessage("¿Estás seguro que deseas abandonar \"${binding.tvComunidadNombre.text}\"? Perderás acceso al contenido privado.")
            .setPositiveButton("Abandonar") { _, _ -> Toast.makeText(this, "Abandonaste el grupo", Toast.LENGTH_SHORT).show(); finish() }
            .setNegativeButton("Cancelar", null).show()
    }

    private fun confirmarReportarGrupo() {
        val razones = arrayOf("Contenido inapropiado", "Spam", "Desinformación", "Acoso o bullying", "Otro")
        AlertDialog.Builder(this).setTitle("🚩 Reportar grupo").setItems(razones) { _, which ->
            Toast.makeText(this, "Reporte enviado: ${razones[which]}. Gracias.", Toast.LENGTH_LONG).show()
        }.setNegativeButton("Cancelar", null).show()
    }
}
