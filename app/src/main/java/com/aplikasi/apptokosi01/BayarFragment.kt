package com.aplikasi.apptokosi01

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.apptokosi01.adapter.CartAdapter
import com.aplikasi.apptokosi01.api.BaseRetrofit
import com.aplikasi.apptokosi01.response.cart.Cart
import com.aplikasi.apptokosi01.response.itemTransaksi.ItemTransaksiResponsePost
import com.aplikasi.apptokosi01.response.produk.ProdukResponsePost
import com.aplikasi.apptokosi01.response.transaksi.TransaksiResponsePost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class BayarFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bayar, container, false)

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        val total = arguments?.getString("TOTAL")
        val myCart = arguments?.getParcelableArrayList<Cart>("MY_CART")

        val rv = view.findViewById(R.id.rvCart) as RecyclerView

        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(activity)
        val rvAdapter = myCart?.let { CartAdapter(it) }
        rv.adapter = rvAdapter

        val txtTotalTransaksiBayar = view.findViewById<TextView>(R.id.txtTotalTransaksiBayar)
        val txtKembalian = view.findViewById<TextView>(R.id.txtKembalian)

        txtTotalTransaksiBayar?.text = numberFormat.format(total?.toDouble()).toString()

        val txtPenerimaan = view.findViewById<EditText>(R.id.txtPenerimaan)
        txtPenerimaan.addTextChangedListener( object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var kembalian = 0

                if (txtPenerimaan.text.toString() != "") {
                    val penerimaan: Int = txtPenerimaan.text.toString().toInt()
                    kembalian = penerimaan - total.toString().toInt()

                    if (kembalian > 0) {
                        txtKembalian.text = numberFormat.format(kembalian.toDouble()).toString()
                    }
                    else {
                        txtKembalian.text = numberFormat.format(0).toString()
                    }

                }
            }

            override fun afterTextChanged(p0: Editable?) {


            }
        } )

        val token = LoginActivity.sessionManager.getString("TOKEN")
        val admin_id = LoginActivity.sessionManager.getString("ADMIN_ID")

        val btnSimpanBayar = view.findViewById<Button>(R.id.btnSimpanBayar)
        btnSimpanBayar.setOnClickListener {
            api.postTransaksi(
                token.toString(),
                admin_id.toString().toInt(),
                total.toString().toInt()
            ).enqueue(object :
                Callback<TransaksiResponsePost> {
                override fun onResponse(
                    call: Call<TransaksiResponsePost>,
                    response: Response<TransaksiResponsePost>
                ) {
                    Log.d("PostTransaksiSukses", response.body().toString())
                    Toast.makeText(context, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT)
                        .show()

                    val idTransaksi = response.body()!!.data.transaksi.id

                    for (item in myCart!!)
                        api.postItemTransaksi(
                            token.toString(),
                            idTransaksi.toString().toInt(),
                            item.id,
                            item.qty,
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
                    findNavController().navigate(R.id.transaksiFragment)

                }

                override fun onFailure(call: Call<TransaksiResponsePost>, t: Throwable) {
                    Log.e("PostTransaksiError", t.toString())
                }

            })
        }

        return view
    }

}