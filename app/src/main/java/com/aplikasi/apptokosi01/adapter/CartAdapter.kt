package com.aplikasi.apptokosi01.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.apptokosi01.R
import com.aplikasi.apptokosi01.api.BaseRetrofit
import com.aplikasi.apptokosi01.response.cart.Cart
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.*

class CartAdapter(val listCart: List<Cart>):RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val api by lazy { BaseRetrofit().endpoint }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cart = listCart[position]
        val totalHarga = cart.harga.toDouble() * cart.qty

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

//        holder.txtCartID.text = "#ID-" + cart.id.toString()
        holder.txtCartID.text = cart.name
        holder.txtCartHarga.text = numberFormat.format(totalHarga).toString()
        holder.txtCartQty.text = "qty: " + cart.qty.toString()
    }

    override fun getItemCount(): Int {
        return listCart.size
    }

    class ViewHolder(itemViem: View) : RecyclerView.ViewHolder(itemViem) {
        val txtCartID = itemViem.findViewById(R.id.txtCartID) as TextView
        val txtCartHarga = itemViem.findViewById(R.id.txtCartHarga) as TextView
        val txtCartQty = itemViem.findViewById(R.id.txtCartQty) as TextView
    }
}