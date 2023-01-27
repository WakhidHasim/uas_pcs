package com.aplikasi.apptokosi01.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.apptokosi01.CallbackInterface
import com.aplikasi.apptokosi01.R
import com.aplikasi.apptokosi01.response.cart.Cart
import com.aplikasi.apptokosi01.response.produk.Produk
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class TransaksiAdapter(val listProduk: List<Produk>): RecyclerView.Adapter<TransaksiAdapter.ViewHolder>() {

    var callbackInterface: CallbackInterface? = null
    var total: Int = 0
    var cart: ArrayList<Cart> = arrayListOf()

    class ViewHolder (itemViem: View) : RecyclerView.ViewHolder(itemViem) {
        val txtNamaProduk = itemViem.findViewById(R.id.txtNamaProduk) as TextView
        val txtHarga = itemViem.findViewById(R.id.txtHarga) as TextView
        val txtQty = itemViem.findViewById(R.id.txtQty) as TextView
        val btnPlus = itemViem.findViewById(R.id.btnPlus) as MaterialButton
        val btnMinus = itemViem.findViewById(R.id.btnMinus) as MaterialButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaksi, parent, false)
        return TransaksiAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listProduk.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        holder.txtNamaProduk.text = produk.nama

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        holder.txtHarga.text = numberFormat.format(produk.harga.toDouble()).toString()

        holder.btnPlus.setOnClickListener{

            val oldVal = holder.txtQty.text.toString().toInt()
            val newVal = oldVal + 1;

            holder.txtQty.text = newVal.toString()

            total += produk.harga.toInt()
//            holder.txtHarga.text = numberFormat.format(total.toDouble()).toString()

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

//            holder.txtHarga.text = numberFormat.format(total.toDouble()).toString()

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