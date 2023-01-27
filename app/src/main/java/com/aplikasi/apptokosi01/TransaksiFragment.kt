package com.aplikasi.apptokosi01

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.apptokosi01.adapter.ProdukAdapter
import com.aplikasi.apptokosi01.adapter.TransaksiAdapter
import com.aplikasi.apptokosi01.api.BaseRetrofit
import com.aplikasi.apptokosi01.response.cart.Cart
import com.aplikasi.apptokosi01.response.produk.Produk
import com.aplikasi.apptokosi01.response.produk.ProdukResponse
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class TransaksiFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    private lateinit var myCart: kotlin.collections.ArrayList<Cart>
    private var totalBayar: String? = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaksi, container, false)

        getProduk(view)

        val btnBayar = view.findViewById<Button>(R.id.btnBayar)
        btnBayar.setOnClickListener{

            if (totalBayar == null || totalBayar!!.toInt() <= 0) {
                Toast.makeText(context, "Tidak ada produk dipilih!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bundle = Bundle()
            bundle.putParcelableArrayList("MY_CART", myCart)
            bundle.putString("TOTAL", totalBayar)

            findNavController().navigate(R.id.bayarFragment, bundle)
        }

        return view
    }

    fun getProduk(view:View){
        val token = LoginActivity.sessionManager.getString("TOKEN")

        api.getProduk(token.toString()).enqueue(object : Callback<ProdukResponse> {
            override fun onResponse(
                call: Call<ProdukResponse>,
                response: Response<ProdukResponse>
            ) {
                Log.d("ProdukData",response.body().toString())

                val rv = view.findViewById(R.id.rv_transaksi) as RecyclerView

                rv.setHasFixedSize(true)
                rv.layoutManager = LinearLayoutManager(activity)

                val a = kotlin.collections.ArrayList(response.body()!!.data.produk)
                Collections.reverse(a) // Tampilkan terbaru dulu

                // Filter tersedia
                val listProdukTersedia = kotlin.collections.ArrayList<Produk>()

                a.forEach {
                    if (it.stok.toInt() > 0)
                        listProdukTersedia.add(it)
                }

                val rvAdapter = TransaksiAdapter(listProdukTersedia)
                rv.adapter = rvAdapter

                rvAdapter.callbackInterface = object : CallbackInterface{
                    override fun passResultCallback(total: String, cart: ArrayList<Cart>) {
                        val txtTotalBayar = activity?.findViewById<TextView>(R.id.txtTotalBayar)

                        val localeID = Locale("in", "ID")
                        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

                        txtTotalBayar?.text = numberFormat.format(total.toDouble()).toString()

                        totalBayar = total
                        myCart = cart

                        Log.d("MyCart", cart.toString())

                    }

                }

            }

            override fun onFailure(call: Call<ProdukResponse>, t: Throwable) {
                Log.e("ProdukError",t.toString())
            }
        })
    }

}