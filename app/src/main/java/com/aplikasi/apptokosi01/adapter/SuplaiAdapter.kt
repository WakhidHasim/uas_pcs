package com.aplikasi.apptokosi01.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.apptokosi01.CallbackInterface
import com.aplikasi.apptokosi01.R
import com.aplikasi.apptokosi01.response.cart.Cart
import com.aplikasi.apptokosi01.response.produk.Produk
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.*

class SuplaiAdapter(var listProduk: List<Produk>): RecyclerView.Adapter<SuplaiAdapter.ViewHolder>() {

    var callbackInterface: CallbackInterface? = null
    var total: Int = 0
    var cart: ArrayList<Cart> = arrayListOf()

    class ViewHolder(itemViem: View) : RecyclerView.ViewHolder(itemViem) {
        val txtNamaProduk = itemViem.findViewById(R.id.txtNamaProduk) as TextView
        val txtHarga = itemViem.findViewById(R.id.txtHarga) as TextView
        val txtStok = itemViem.findViewById(R.id.txtStok) as TextView
        val txtKeuntungan = itemViem.findViewById(R.id.txtKeuntungan) as TextView
        val txtQty = itemViem.findViewById(R.id.txtQty) as TextView
        val btnPlus = itemViem.findViewById(R.id.btnPlus) as MaterialButton
        val btnMinus = itemViem.findViewById(R.id.btnMinus) as MaterialButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_suplai, parent, false)
        return SuplaiAdapter.ViewHolder(view)
    }

    fun setProduk(listProduk: List<Produk>) {
        this.listProduk = listProduk;
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listProduk.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        val hargaAsli = produk.harga;
        produk.harga = Math.ceil(produk.harga.toDouble() * 0.85).toInt().toString()
        val keuntungan = hargaAsli.toInt() - produk.harga.toInt();

        holder.txtNamaProduk.text = produk.nama

        var textStok = ""

        if (produk.stok.toInt() > 0) {
            textStok = "Sisa stok " + produk.stok
            holder.txtStok.setTextColor(holder.txtNamaProduk.context.resources.getColor(com.google.android.material.R.color.m3_default_color_secondary_text))
        }
        else {
            textStok = "Stok habis"
            holder.txtStok.setTextColor(Color.RED)
        }

        holder.txtStok.text = textStok

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        holder.txtHarga.text = numberFormat.format(produk.harga.toDouble()).toString()
        holder.txtKeuntungan.text = numberFormat.format(keuntungan.toDouble()).toString()

        holder.btnPlus.setOnClickListener{

            val oldVal = holder.txtQty.text.toString().toInt()
            val newVal = oldVal + 1;

            holder.txtQty.text = newVal.toString()

            total += produk.harga.toInt()

            val index = cart.indexOfFirst { it.id == produk.id.toInt() }

            if (index != -1) {
                cart.removeAt(index)
            }

            val itemCart = Cart(
                produk.id.toString().toInt(),
                produk.nama,
                produk.harga.toInt(),
                newVal
            )
            cart.add(itemCart)

            callbackInterface?.passResultCallback(total.toString(),cart)

        }

        holder.btnMinus.setOnClickListener{

            val oldVal = holder.txtQty.text.toString().toInt()
            val newVal = oldVal - 1;

            if (newVal >= 0) {
                holder.txtQty.text = newVal.toString()
                total -= produk.harga.toInt()
            }

            val index = cart.indexOfFirst { it.id == produk.id.toInt() }

            if (index != -1) {
                cart.removeAt(index)
            }

            if (newVal > 0) {
                val itemCart = Cart(
                    produk.id.toInt(),
                    produk.nama,
                    produk.harga.toInt(),
                    newVal
                )
                cart.add(itemCart)
            }

            callbackInterface?.passResultCallback(total.toString(),cart)

        }

    }

}