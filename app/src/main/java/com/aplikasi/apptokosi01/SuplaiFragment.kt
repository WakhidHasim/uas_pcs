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
import com.aplikasi.apptokosi01.adapter.SuplaiAdapter
import com.aplikasi.apptokosi01.adapter.TransaksiAdapter
import com.aplikasi.apptokosi01.api.BaseRetrofit
import com.aplikasi.apptokosi01.model.OptionsMenu
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

class SuplaiFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    private lateinit var myCart: kotlin.collections.ArrayList<Cart>
    private var totalBayar: String? = "0"
    lateinit var listProduk: kotlin.collections.List<Produk>
    lateinit var rv: RecyclerView
    lateinit var rvAdapter: SuplaiAdapter

    lateinit var btnFilterProduk: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_suplai, container, false)

        getProduk(view)

        rv = view.findViewById(R.id.rvSuplai) as RecyclerView

        btnFilterProduk = view.findViewById<MaterialButton>(R.id.btnProdukFilter)

        val menu = OptionsMenu(context, btnFilterProduk, R.menu.menu_list_produk)
        menu.popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_filter_seluruh_produk -> rvAdapter.setProduk(listProduk)
                R.id.menu_item_filter_produk_tersedia -> {

                    val listProdukTersedia = kotlin.collections.ArrayList<Produk>()

                    listProduk.forEach {
                        if (it.stok.toInt() > 0)
                            listProdukTersedia.add(it)
                    }

                    rvAdapter.setProduk(listProdukTersedia)

                }
                R.id.menu_item_filter_produk_habis -> {

                    val listProdukHabis = kotlin.collections.ArrayList<Produk>()

                    listProduk.forEach {
                        if (it.stok.toInt() <= 0)
                            listProdukHabis.add(it)
                    }

                    rvAdapter.setProduk(listProdukHabis)

                }
            }
            return@setOnMenuItemClickListener true
        }
        btnFilterProduk.setOnClickListener {
            menu.show()
        }

        val btnBayar = view.findViewById<Button>(R.id.btnBayar)
        btnBayar.setOnClickListener{

            if (totalBayar == null || totalBayar!!.toInt() <= 0) {
                Toast.makeText(context, "Tidak ada produk dipilih!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bundle = Bundle()
            bundle.putParcelableArrayList("SUPPLY_CART", myCart)
            bundle.putString("SUPPLY_COST", totalBayar)

            findNavController().navigate(R.id.bayarSuplaiFragment, bundle)
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

                val listProdukHabis = kotlin.collections.ArrayList<Produk>()

                a.forEach {
                    if (it.stok.toInt() <= 0)
                        listProdukHabis.add(it)
                }

                listProduk = kotlin.collections.ArrayList(listProdukHabis)
                listProdukTersedia.forEach {
                    (listProduk as ArrayList<Produk>).add(it)
                }

                rvAdapter = SuplaiAdapter(listProduk)
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

//                btnFilterProduk.visibility = View.VISIBLE

            }

            override fun onFailure(call: Call<ProdukResponse>, t: Throwable) {
                Log.e("ProdukError",t.toString())
            }
        })
    }

}