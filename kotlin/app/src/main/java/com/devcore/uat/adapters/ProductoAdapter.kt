package com.devcore.uat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devcore.uat.databinding.ItemProductoBinding
import com.devcore.uat.network.ProductoResponse
import java.text.NumberFormat
import java.util.Locale

class ProductoAdapter(
    private var productos: List<ProductoResponse> = emptyList(),
    private val onItemClick: (ProductoResponse) -> Unit = {}
) : RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {

    fun updateData(newList: List<ProductoResponse>) {
        productos = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount() = productos.size

    inner class ViewHolder(private val binding: ItemProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: ProductoResponse) {
            binding.tvProductTitle.text = producto.titulo
            
            // Formato de precio en MXN
            val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
            binding.tvProductPrice.text = fmt.format(producto.precio)
            
            // Condicion
            binding.tvProductCondition.text = when (producto.condicion) {
                "nuevo" -> "Nuevo"
                "como_nuevo" -> "Como nuevo"
                "usado" -> "Usado"
                else -> producto.condicion
            }
            
            // Imagen o Emoji
            if (!producto.imagen_url.isNullOrEmpty()) {
                binding.ivProductImage.visibility = android.view.View.VISIBLE
                binding.tvProductIcon.visibility = android.view.View.GONE
                val fullUrl = "http://10.0.2.2:8000${producto.imagen_url}"
                com.bumptech.glide.Glide.with(binding.root)
                    .load(fullUrl)
                    .centerCrop()
                    .into(binding.ivProductImage)
            } else {
                binding.ivProductImage.visibility = android.view.View.GONE
                binding.tvProductIcon.visibility = android.view.View.VISIBLE
                binding.tvProductIcon.text = when (producto.categoria) {
                    "libros" -> "\uD83D\uDCDA"       // 📚
                    "electronicos" -> "\uD83D\uDCBB"  // 💻
                    "ropa" -> "\uD83D\uDC55"          // 👕
                    "accesorios" -> "\uD83C\uDF92"    // 🎒
                    else -> "\uD83D\uDCE6"             // 📦
                }
            }
            
            // Vendedor
            binding.tvProductSeller.text = producto.vendedor?.nombre ?: "Vendedor"
            
            binding.root.setOnClickListener { onItemClick(producto) }
        }
    }
}
