package com.devcore.uat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devcore.uat.network.ReporteBusResponse
import java.text.SimpleDateFormat
import java.util.*

class BusReporteAdapter(
    private var reportes: List<ReporteBusResponse>,
    private val onConfirmarClick: (ReporteBusResponse) -> Unit
) : RecyclerView.Adapter<BusReporteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tvIcon)
        val tvReporteTitulo: TextView = view.findViewById(R.id.tvReporteTitulo)
        val tvReporteAutor: TextView = view.findViewById(R.id.tvReporteAutor)
        val tvConfirmaciones: TextView = view.findViewById(R.id.tvConfirmaciones)
        val cardConfirmar: CardView = view.findViewById(R.id.cardConfirmar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reporte_bus, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reporte = reportes[position]

        if (reporte.tipo == "ya_paso") {
            holder.tvIcon.text = "✅"
            holder.tvReporteTitulo.text = "Ya pasó${if(reporte.zona != null) " por " + reporte.zona else ""}"
            holder.tvConfirmaciones.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.verde_pastel))
        } else {
            holder.tvIcon.text = "❌"
            holder.tvReporteTitulo.text = "No ha pasado${if(reporte.zona != null) " por " + reporte.zona else ""}"
            holder.tvConfirmaciones.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.rojo_pastel))
        }

        // Formatear fecha
        val tiempoStr = try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(reporte.timestamp)
            
            if (date != null) {
                val diff = Date().time - date.time
                val minutos = diff / (60 * 1000)
                if (minutos <= 0) "Hace un momento"
                else if (minutos < 60) "Hace $minutos min"
                else "Hace ${minutos / 60} h"
            } else {
                "Hace poco"
            }
        } catch (e: Exception) {
            "Hace poco"
        }

        holder.tvReporteAutor.text = "Reportado por ${reporte.autor_nombre} • $tiempoStr"
        holder.tvConfirmaciones.text = "${reporte.confirmaciones} confirman"

        holder.cardConfirmar.setOnClickListener {
            onConfirmarClick(reporte)
        }
    }

    override fun getItemCount() = reportes.size

    fun updateData(newReportes: List<ReporteBusResponse>) {
        reportes = newReportes
        notifyDataSetChanged()
    }
}
