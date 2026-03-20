package com.devcore.uat

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devcore.uat.databinding.ActivityMarketplaceBinding
import com.google.android.material.button.MaterialButton

class MarketplaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarketplaceBinding

    private enum class SellerFilter { ALL, SPONSOR, STUDENT }
    private enum class SellerType { SPONSOR, STUDENT }

    private data class MarketplaceProduct(
        val id: Int,
        var name: String,
        var price: Double,
        var category: String,
        var sellerType: SellerType,
        var description: String,
        var imageRes: Int,
        var imageUri: String? = null,
        var imageBitmap: Bitmap? = null,
        var isMine: Boolean = false
    )

    private val categories = listOf("Libros", "Electronicos", "Ropa", "Accesorios", "Comida")
    private val allProducts = mutableListOf<MarketplaceProduct>()
    private var nextProductId = 1

    private var sellerFilter = SellerFilter.ALL
    private var categoryFilter: String? = null
    private var searchQuery = ""

    private lateinit var adapter: MarketplaceProductAdapter

    private var onProductImagePicked: ((Uri) -> Unit)? = null
    private var onProductPhotoPicked: ((Bitmap) -> Unit)? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            onProductImagePicked?.invoke(uri)
        }
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            onProductPhotoPicked?.invoke(bitmap)
        }
    }

    private var isPromotersVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        seedProducts()
        setupRecycler()
        setupClickListeners()
        applyFilters()
    }

    private fun setupRecycler() {
        adapter = MarketplaceProductAdapter()
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter

        binding.rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 8) {
                    updatePromotersVisibility(show = false)
                } else if (dy < -8) {
                    updatePromotersVisibility(show = true)
                }
            }
        })
    }

    private fun updatePromotersVisibility(show: Boolean) {
        if (show == isPromotersVisible) return
        isPromotersVisible = show

        val section = binding.sectionPromotores
        section.animate().cancel()

        if (show) {
            section.visibility = android.view.View.VISIBLE
            section.alpha = 0f
            section.translationY = -20f
            section.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(180)
                .start()
        } else {
            section.animate()
                .alpha(0f)
                .translationY(-20f)
                .setDuration(150)
                .withEndAction {
                    section.visibility = android.view.View.GONE
                    section.alpha = 1f
                    section.translationY = 0f
                }
                .start()
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnBuscar.setOnClickListener {
            searchQuery = binding.etBuscar.text?.toString()?.trim().orEmpty()
            applyFilters()
        }

        binding.fabPublicar.setOnClickListener {
            showProductDialog(null)
        }

        binding.btnEditarPublicacion.setOnClickListener {
            val mine = allProducts.firstOrNull { it.isMine }
            if (mine == null) {
                Toast.makeText(this, "Primero publica un producto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showProductDialog(mine)
        }

        binding.btnCatLibros.setOnClickListener { setCategoryFilter("Libros") }
        binding.btnCatElectronicos.setOnClickListener { setCategoryFilter("Electronicos") }
        binding.btnCatRopa.setOnClickListener { setCategoryFilter("Ropa") }
        binding.btnCatAccesorios.setOnClickListener { setCategoryFilter("Accesorios") }
        binding.btnCatComida.setOnClickListener { setCategoryFilter("Comida") }
        binding.btnCatTodos.setOnClickListener { setCategoryFilter(null) }

        binding.chipTodos.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sellerFilter = SellerFilter.ALL
                applyFilters()
            }
        }
        binding.chipPatrocinadores.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sellerFilter = SellerFilter.SPONSOR
                applyFilters()
            }
        }
        binding.chipAlumnos.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sellerFilter = SellerFilter.STUDENT
                applyFilters()
            }
        }

        binding.root.findViewById<android.view.View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)); finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navBus)?.setOnClickListener {
            startActivity(Intent(this, BusTrackingActivity::class.java)); finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navComunidades)?.setOnClickListener {
            startActivity(Intent(this, ComunidadesActivity::class.java)); finish()
        }
        binding.root.findViewById<android.view.View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)); finish()
        }
    }

    private fun setCategoryFilter(category: String?) {
        categoryFilter = category
        applyFilters()
        val msg = if (category == null) "Mostrando todas las categorias" else "Categoria: $category"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun applyFilters() {
        val query = searchQuery.lowercase()
        val filtered = allProducts.filter { product ->
            val bySeller = when (sellerFilter) {
                SellerFilter.ALL -> true
                SellerFilter.SPONSOR -> product.sellerType == SellerType.SPONSOR
                SellerFilter.STUDENT -> product.sellerType == SellerType.STUDENT
            }

            val byCategory = categoryFilter == null || product.category.equals(categoryFilter, ignoreCase = true)

            val bySearch = query.isBlank() ||
                product.name.lowercase().contains(query) ||
                product.description.lowercase().contains(query) ||
                product.category.lowercase().contains(query)

            bySeller && byCategory && bySearch
        }

        adapter.submit(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun showProductDialog(product: MarketplaceProduct?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_marketplace_product, null)
        val etName = dialogView.findViewById<EditText>(R.id.etProductName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etProductPrice)
        val etDescription = dialogView.findViewById<EditText>(R.id.etProductDescription)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val sellerGroup = dialogView.findViewById<RadioGroup>(R.id.rgSellerType)
        val rbStudent = dialogView.findViewById<RadioButton>(R.id.rbStudent)
        val ivPreview = dialogView.findViewById<ImageView>(R.id.ivProductPreview)
        val btnSelectImage = dialogView.findViewById<MaterialButton>(R.id.btnSelectProductImage)
        val btnTakePhoto = dialogView.findViewById<MaterialButton>(R.id.btnTakeProductPhoto)

        var selectedImageUri: String? = product?.imageUri
        var selectedBitmap: Bitmap? = product?.imageBitmap

        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        fun renderPreview() {
            val selectedCategory = spinnerCategory.selectedItem?.toString().orEmpty()
            when {
                selectedBitmap != null -> ivPreview.setImageBitmap(selectedBitmap)
                !selectedImageUri.isNullOrEmpty() -> ivPreview.setImageURI(Uri.parse(selectedImageUri))
                else -> ivPreview.setImageResource(imageForCategory(selectedCategory))
            }
        }

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (selectedImageUri.isNullOrEmpty() && selectedBitmap == null) {
                    renderPreview()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        btnSelectImage.setOnClickListener {
            onProductImagePicked = { uri ->
                selectedBitmap = null
                selectedImageUri = uri.toString()
                renderPreview()
            }
            pickImageLauncher.launch("image/*")
        }

        btnTakePhoto.setOnClickListener {
            onProductPhotoPicked = { bitmap ->
                selectedImageUri = null
                selectedBitmap = bitmap
                renderPreview()
            }
            takePhotoLauncher.launch(null)
        }

        if (product != null) {
            etName.setText(product.name)
            etPrice.setText(product.price.toString())
            etDescription.setText(product.description)
            val categoryIndex = categories.indexOfFirst { it.equals(product.category, ignoreCase = true) }
            if (categoryIndex >= 0) {
                spinnerCategory.setSelection(categoryIndex)
            }
            if (product.sellerType == SellerType.STUDENT) {
                rbStudent.isChecked = true
            }
        }
        renderPreview()

        val isEditing = product != null
        val dialog = AlertDialog.Builder(this)
            .setTitle(if (isEditing) "Editar producto" else "Publicar producto")
            .setView(dialogView)
            .setPositiveButton(if (isEditing) "Guardar" else "Publicar", null)
            .setNegativeButton("Cancelar") { _, _ ->
                onProductImagePicked = null
                onProductPhotoPicked = null
            }
            .create()

        dialog.setOnDismissListener {
            onProductImagePicked = null
            onProductPhotoPicked = null
        }

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = etName.text.toString().trim()
                val priceText = etPrice.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val category = spinnerCategory.selectedItem?.toString().orEmpty()
                val sellerType = if (sellerGroup.checkedRadioButtonId == R.id.rbSponsor) SellerType.SPONSOR else SellerType.STUDENT

                if (name.isEmpty() || priceText.isEmpty() || description.isEmpty()) {
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val price = priceText.toDoubleOrNull()
                if (price == null || price <= 0.0) {
                    Toast.makeText(this, "Precio invalido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (isEditing) {
                    val existing = product ?: return@setOnClickListener
                    existing.name = name
                    existing.price = price
                    existing.description = description
                    existing.category = category
                    existing.sellerType = sellerType
                    existing.imageRes = imageForCategory(category)
                    existing.imageUri = selectedImageUri
                    existing.imageBitmap = selectedBitmap
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    allProducts.add(
                        0,
                        MarketplaceProduct(
                            id = nextProductId++,
                            name = name,
                            price = price,
                            description = description,
                            category = category,
                            sellerType = sellerType,
                            imageRes = imageForCategory(category),
                            imageUri = selectedImageUri,
                            imageBitmap = selectedBitmap,
                            isMine = true
                        )
                    )
                    Toast.makeText(this, "Producto publicado", Toast.LENGTH_SHORT).show()
                }

                applyFilters()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun seedProducts() {
        if (allProducts.isNotEmpty()) return

        val demo = listOf(
            MarketplaceProduct(nextProductId++, "Calculadora TI-84", 800.0, "Electronicos", SellerType.STUDENT, "Como nueva, con funda", imageForCategory("Electronicos")),
            MarketplaceProduct(nextProductId++, "Libro Calculo Diferencial", 220.0, "Libros", SellerType.STUDENT, "Subrayado ligero, buen estado", imageForCategory("Libros")),
            MarketplaceProduct(nextProductId++, "Mochila UAT", 320.0, "Accesorios", SellerType.STUDENT, "Poco uso", imageForCategory("Accesorios")),
            MarketplaceProduct(nextProductId++, "Set de plumones", 90.0, "Accesorios", SellerType.STUDENT, "12 colores", imageForCategory("Accesorios")),
            MarketplaceProduct(nextProductId++, "Playera oficial UAT", 180.0, "Ropa", SellerType.STUDENT, "Talla M", imageForCategory("Ropa")),
            MarketplaceProduct(nextProductId++, "Combo hamburguesa", 75.0, "Comida", SellerType.SPONSOR, "Promo alumno 2x1", imageForCategory("Comida")),
            MarketplaceProduct(nextProductId++, "Servicio de reparacion laptop", 250.0, "Electronicos", SellerType.SPONSOR, "Diagnostico gratis", imageForCategory("Electronicos")),
            MarketplaceProduct(nextProductId++, "Cuaderno universitario", 35.0, "Libros", SellerType.SPONSOR, "Mayoreo disponible", imageForCategory("Libros")),
            MarketplaceProduct(nextProductId++, "Paquete desayuno", 55.0, "Comida", SellerType.SPONSOR, "Incluye cafe", imageForCategory("Comida")),
            MarketplaceProduct(nextProductId++, "Sudadera UAT", 399.0, "Ropa", SellerType.SPONSOR, "Colores surtidos", imageForCategory("Ropa"))
        )

        allProducts.addAll(demo)
    }

    private fun imageForCategory(category: String): Int {
        return when (category.lowercase()) {
            "libros" -> android.R.drawable.ic_menu_edit
            "electronicos" -> android.R.drawable.ic_menu_manage
            "ropa" -> android.R.drawable.ic_menu_gallery
            "accesorios" -> android.R.drawable.ic_menu_myplaces
            "comida" -> android.R.drawable.ic_menu_compass
            else -> android.R.drawable.ic_menu_report_image
        }
    }

    private fun sellerLabel(type: SellerType): String {
        return if (type == SellerType.SPONSOR) "Patrocinador" else "Alumno"
    }

    private fun showProductDetailDialog(product: MarketplaceProduct) {
        val view = layoutInflater.inflate(R.layout.dialog_marketplace_product_detail, null)
        val ivProduct = view.findViewById<ImageView>(R.id.ivDetailProduct)
        val tvName = view.findViewById<TextView>(R.id.tvDetailName)
        val tvPrice = view.findViewById<TextView>(R.id.tvDetailPrice)
        val tvSeller = view.findViewById<TextView>(R.id.tvDetailSeller)
        val tvCategory = view.findViewById<TextView>(R.id.tvDetailCategory)
        val tvDescription = view.findViewById<TextView>(R.id.tvDetailDescription)

        bindProductImage(ivProduct, product)
        tvName.text = product.name
        tvPrice.text = "$${"%.2f".format(product.price)}"
        tvSeller.text = "Vendedor: ${sellerLabel(product.sellerType)}"
        tvCategory.text = "Categoria: ${product.category}"
        tvDescription.text = product.description

        AlertDialog.Builder(this)
            .setTitle("Detalle del producto")
            .setView(view)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private inner class MarketplaceProductAdapter : RecyclerView.Adapter<MarketplaceProductViewHolder>() {
        private val items = mutableListOf<MarketplaceProduct>()

        fun submit(newItems: List<MarketplaceProduct>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): MarketplaceProductViewHolder {
            val view = layoutInflater.inflate(R.layout.item_marketplace_product, parent, false)
            return MarketplaceProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: MarketplaceProductViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size
    }

    private inner class MarketplaceProductViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumb = itemView.findViewById<ImageView>(R.id.ivProductThumb)
        private val tvName = itemView.findViewById<android.widget.TextView>(R.id.tvProductName)
        private val tvPrice = itemView.findViewById<android.widget.TextView>(R.id.tvProductPrice)
        private val tvMeta = itemView.findViewById<android.widget.TextView>(R.id.tvProductMeta)
        private val tvDescription = itemView.findViewById<android.widget.TextView>(R.id.tvProductDescription)
        private val chipSeller = itemView.findViewById<com.google.android.material.chip.Chip>(R.id.chipSellerType)

        fun bind(product: MarketplaceProduct) {
            tvName.text = product.name
            tvPrice.text = "$${"%.2f".format(product.price)}"
            tvMeta.text = "${product.category} • ID ${product.id}"
            tvDescription.text = product.description

            chipSeller.text = sellerLabel(product.sellerType)
            chipSeller.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                getColor(if (product.sellerType == SellerType.SPONSOR) R.color.uat_naranja_claro else R.color.uat_azul_claro)
            )

            itemView.setOnClickListener {
                showProductDetailDialog(product)
            }

            bindProductImage(ivThumb, product)
        }
    }

    private fun bindProductImage(imageView: ImageView, product: MarketplaceProduct) {
        when {
            product.imageBitmap != null -> imageView.setImageBitmap(product.imageBitmap)
            !product.imageUri.isNullOrEmpty() -> imageView.setImageURI(Uri.parse(product.imageUri))
            else -> imageView.setImageResource(product.imageRes)
        }
    }
}
