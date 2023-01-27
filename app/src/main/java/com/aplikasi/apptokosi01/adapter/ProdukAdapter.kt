package com.aplikasi.apptokosi01.adapter

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.apptokosi01.LoginActivity
import com.aplikasi.apptokosi01.R
import com.aplikasi.apptokosi01.api.BaseRetrofit
import com.aplikasi.apptokosi01.model.OptionsMenu
import com.aplikasi.apptokosi01.response.produk.Produk
import com.aplikasi.apptokosi01.response.produk.ProdukResponse
import com.aplikasi.apptokosi01.response.produk.ProdukResponsePost
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class ProdukAdapter(var listProduk: List<Produk>):RecyclerView.Adapter<ProdukAdapter.ViewHolder>() {

    private val api by lazy { BaseRetrofit().endpoint }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return ViewHolder(view)
    }

    fun setProduk(listProduk: List<Produk>) {
        this.listProduk = listProduk;
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        val context = holder.txtNamaProduk.context

        var textStok = ""

        if (produk.stok.toInt() > 0) {
            textStok = "Stok " + produk.stok + " unit"
            holder.txtProdukStok.setTextColor(holder.txtNamaProduk.context.resources.getColor(com.google.android.material.R.color.m3_default_color_secondary_text))

            holder.textProdukKeterangan.text = "Produk tersedia"
            holder.textProdukKeterangan.setTextColor(Color.parseColor("#4CAF50"))
        }
        else {
            textStok = "Stok kosong"
            holder.txtProdukStok.setTextColor(Color.RED)

            holder.textProdukKeterangan.text = "Harap segera menambah stok!"
            holder.textProdukKeterangan.setTextColor(Color.RED)
        }

        holder.txtNamaProduk.text = produk.nama
        holder.txtProdukStok.text = textStok

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        holder.txtHarga.text = numberFormat.format(produk.harga.toDouble()).toString()

        val token = LoginActivity.sessionManager.getString("TOKEN")

        holder.cardProduk.setOnClickListener{
            val bundle = Bundle();
            bundle.putParcelable("produk", produk)
            bundle.putString("status", "edit")

            holder.itemView.findNavController().navigate(R.id.produkFormFragment, bundle)
        }

        val menu = OptionsMenu(context, holder.cardProduk, R.menu.menu_produk)
        menu.popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_item_produk_hapus -> {
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Produk \"" + produk.nama + "\" akan dihapus. Tindakan ini tidak dapat dibatalkan.")
                        .setPositiveButton("Hapus") { dialogInterface, i ->
                            api.deleteProduk(token.toString(), produk.id.toInt()).enqueue(
                                object : Callback<ProdukResponsePost> {
                                    override fun onResponse(
                                        call: Call<ProdukResponsePost>,
                                        response: Response<ProdukResponsePost>
                                    ) {
                                        Log.d("HapusProdukBerhasil", response.body().toString())

                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Data berhasil dihapus",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        holder.itemView.findNavController()
                                            .navigate(R.id.produkFragment)
                                    }

                                    override fun onFailure(
                                        call: Call<ProdukResponsePost>,
                                        t: Throwable
                                    ) {
                                        Log.e("HapusProdukError", t.toString())
                                    }
                                })
                        }
                        .setNeutralButton("Batal", null)
                        .show()


                }
            }
            return@setOnMenuItemClickListener true
        }
        holder.cardProduk.setOnLongClickListener {
            menu.show()
            return@setOnLongClickListener true
        }

    }

    override fun getItemCount(): Int {
        return listProduk.size
    }

    class ViewHolder(itemViem: View) : RecyclerView.ViewHolder(itemViem) {
        val cardProduk = itemViem.findViewById(R.id.cardProduk) as MaterialCardView
        val txtNamaProduk = itemViem.findViewById(R.id.txtNamaProduk) as TextView
        val txtProdukStok = itemViem.findViewById(R.id.txtProdukStok) as TextView
        val textProdukKeterangan = itemViem.findViewById(R.id.textProdukKeterangan) as TextView
        val txtHarga = itemViem.findViewById(R.id.txtHarga) as TextView
        val btnEdit = itemViem.findViewById(R.id.btnEdit) as MaterialButton
        val btnDelete = itemViem.findViewById(R.id.btnDelete) as MaterialButton
    }
}