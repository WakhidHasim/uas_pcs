package com.aplikasi.apptokosi01.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.apptokosi01.R
import com.aplikasi.apptokosi01.response.transaksi.Transaksi
import java.text.NumberFormat
import java.util.*

class LaporanAdapter(val listTransaksi: List<Transaksi>): RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val txtTglTransaksi = itemView.findViewById<TextView>(R.id.txtTglTransaksi)
        val txtLaporanAdminID = itemView.findViewById<TextView>(R.id.txtLaporanAdminID)
        val txtNoNota = itemView.findViewById<TextView>(R.id.txtNoNota)
        val txtItemTotalTransaksi = itemView.findViewById<TextView>(R.id.txtTotalItemTransaksi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_laporan, parent, false)
        return LaporanAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTransaksi.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaksi = listTransaksi[position]

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        holder.txtTglTransaksi.text = transaksi.tanggal
        holder.txtLaporanAdminID.text = "Admin-" + transaksi.admin_id
        holder.txtNoNota.text = "Transaksi #ID-"+transaksi.id
        holder.txtItemTotalTransaksi.text = numberFormat.format(transaksi.total.toDouble()).toString()

        if (transaksi.total.toDouble() == 0.0)
            holder.txtItemTotalTransaksi.setTextColor(holder.txtItemTotalTransaksi.context.resources.getColor(com.google.android.material.R.color.m3_default_color_secondary_text))
        else if (transaksi.total.toDouble() > 0)
            holder.txtItemTotalTransaksi.setTextColor(Color.parseColor("#4CAF50"))
        else
            holder.txtItemTotalTransaksi.setTextColor(Color.RED)

    }
}