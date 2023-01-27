package com.aplikasi.apptokosi01

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.apptokosi01.LoginActivity.Companion.sessionManager
import com.aplikasi.apptokosi01.adapter.ProdukAdapter
import com.aplikasi.apptokosi01.api.BaseRetrofit
import com.aplikasi.apptokosi01.model.OptionsMenu
import com.aplikasi.apptokosi01.response.produk.Produk
import com.aplikasi.apptokosi01.response.produk.ProdukResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProdukFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    lateinit var listProduk: kotlin.collections.List<Produk>
    lateinit var rv: RecyclerView
    lateinit var txtTotalProduk: TextView
    lateinit var rvAdapter: ProdukAdapter

    lateinit var btnFilterProduk: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_produk, container, false)

        getProduk(view)

        rv = view.findViewById(R.id.rv_produk) as RecyclerView
        txtTotalProduk = view.findViewById(R.id.txtTotalProduk) as TextView

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

        val btnTambah = view.findViewById<FloatingActionButton>(R.id.btnTambah)
        btnTambah.setOnClickListener{
//            Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show()

            val bundle = Bundle();
            bundle.putString("status", "tambah")

            findNavController().navigate(R.id.produkFormFragment);
        }

        return view
    }

    fun getProduk(view:View){
        val token = sessionManager.getString("TOKEN")

        api.getProduk(token.toString()).enqueue(object : Callback<ProdukResponse> {
            override fun onResponse(
                call: Call<ProdukResponse>,
                response: Response<ProdukResponse>
            ) {
                Log.d("ProdukData",response.body().toString())

                txtTotalProduk.text = response.body()!!.data.produk.size.toString() + " item"

                rv.setHasFixedSize(true)
                rv.layoutManager = LinearLayoutManager(activity)

                listProduk = kotlin.collections.ArrayList(response.body()!!.data.produk)
                Collections.reverse(listProduk) // Tampilkan terbaru dulu

                rvAdapter = ProdukAdapter(listProduk)
                rv.adapter = rvAdapter

                btnFilterProduk.visibility = View.VISIBLE

            }

            override fun onFailure(call: Call<ProdukResponse>, t: Throwable) {
                Log.e("ProdukError",t.toString())
            }
        })
    }
}