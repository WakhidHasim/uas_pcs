package com.aplikasi.apptokosi01

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.apptokosi01.adapter.CartAdapter
import com.aplikasi.apptokosi01.adapter.SuplaiAdapter
import com.aplikasi.apptokosi01.api.BaseRetrofit
import com.aplikasi.apptokosi01.response.cart.Cart
import com.aplikasi.apptokosi01.response.itemTransaksi.ItemTransaksiResponsePost
import com.aplikasi.apptokosi01.response.transaksi.TransaksiResponsePost
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class BayarSuplaiFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bayar_suplai, container, false)

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        val supplyCost = arguments?.getString("SUPPLY_COST")
        val myCart = arguments?.getParcelableArrayList<Cart>("SUPPLY_CART")

        val txtBiayaSuplai = view.findViewById<TextView>(R.id.txtBiayaSuplai)
        txtBiayaSuplai?.text = numberFormat.format(supplyCost?.toDouble()).toString()

        val rv = view.findViewById(R.id.rvCartSuplai) as RecyclerView

        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(activity)
        val rvAdapter = myCart?.let { CartAdapter(it) }
        rv.adapter = rvAdapter

        val token = LoginActivity.sessionManager.getString("TOKEN")
        val admin_id = LoginActivity.sessionManager.getString("ADMIN_ID")

        val btnBayarSuplai = view.findViewById<MaterialButton>(R.id.btnBayarSuplai)
        btnBayarSuplai.setOnClickListener {
            api.postTransaksi(
                token.toString(),
                admin_id.toString().toInt(),
                -supplyCost.toString().toInt()
            ).enqueue(object :
                Callback<TransaksiResponsePost> {
                override fun onResponse(
                    call: Call<TransaksiResponsePost>,
                    response: Response<TransaksiResponsePost>
                ) {
                    Log.d("PostTransaksiSukses", response.body().toString())
                    Toast.makeText(context, "Suplai berhasil dibayar", Toast.LENGTH_SHORT)
                        .show()

                    val idTransaksi = response.body()!!.data.transaksi.id

                    for (item in myCart!!)
                        api.postItemTransaksi(
                            token.toString(),
                            idTransaksi.toInt(),
                            item.id,
                            -item.qty,
                            item.harga
                        ).enqueue(object: Callback<ItemTransaksiResponsePost> {
                            override fun onResponse(
                                call: Call<ItemTransaksiResponsePost>,
                                response: Response<ItemTransaksiResponsePost>
                            ) {
                                Log.d("PostTransaksiSukses", "Sukses")
                            }

                            override fun onFailure(
                                call: Call<ItemTransaksiResponsePost>,
                                t: Throwable
                            ) {
                                Log.e("PostItemTransaksiError", t.toString())
                            }

                        })

                    Toast.makeText(context, "Transaksi disimpan", Toast.LENGTH_SHORT)
                    findNavController().navigate(R.id.suplaiFragment)

                }

                override fun onFailure(call: Call<TransaksiResponsePost>, t: Throwable) {
                    Log.e("PostTransaksiError", t.toString())
                }

            })
        }

        return view
    }
}