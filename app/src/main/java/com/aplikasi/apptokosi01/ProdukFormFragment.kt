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
import com.aplikasi.apptokosi01.api.BaseRetrofit
import com.aplikasi.apptokosi01.response.produk.Produk
import com.aplikasi.apptokosi01.response.produk.ProdukResponsePost
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProdukFormFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_produk_form, container, false)

        val btnFormSimpanProduk = view.findViewById<Button>(R.id.btnFormSimpanProduk)

        val txtFormProdukJudul = view.findViewById<TextView>(R.id.txtFormProdukJudul)
        val txtFormNamaProduk = view.findViewById<TextView>(R.id.txtFormNamaProduk)
        val txtFormHargaProduk = view.findViewById<TextView>(R.id.txtFormHargaProduk)
        val txtFormStokProduk = view.findViewById<TextView>(R.id.txtFormStokProduk)

        val status = arguments?.getString("status")
        val produk = arguments?.getParcelable<Produk>("produk")

        Log.d("ProdukForm", produk.toString())

        if (status == "edit") {
            txtFormProdukJudul.text = "Edit Produk"
            txtFormNamaProduk.text = produk?.nama.toString()
            txtFormHargaProduk.text = produk?.harga.toString()
            txtFormStokProduk.text = produk?.stok .toString()
            btnFormSimpanProduk.text = "Simpan"
        }

        btnFormSimpanProduk.setOnClickListener{

            val txtFormTambahNamaProduk = view.findViewById<TextInputEditText>(R.id.txtFormNamaProduk)
            val txtFormTambahHargaProduk = view.findViewById<TextInputEditText>(R.id.txtFormHargaProduk)
            val txtFormTambahStokProduk = view.findViewById<TextInputEditText>(R.id.txtFormStokProduk)

            if (txtFormTambahNamaProduk.text.toString().length == 0 ||
                txtFormTambahHargaProduk.text.toString().length == 0 ||
                txtFormTambahStokProduk.text.toString().length == 0) {
                Toast.makeText(context, "Form tidak lengkap!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = LoginActivity.sessionManager.getString("TOKEN")
            val admin_id = LoginActivity.sessionManager.getString("ADMIN_ID")

            if (status == "edit") {
                api.putProduk(
                    token.toString(),
                    produk?.id.toString().toInt(),
                    admin_id.toString().toInt(),
                    txtFormTambahNamaProduk.text.toString(),
                    txtFormTambahHargaProduk.text.toString().toInt(),
                    txtFormTambahStokProduk.text.toString().toInt()
                ).enqueue(object :
                    Callback<ProdukResponsePost> {
                    override fun onResponse(
                        call: Call<ProdukResponsePost>,
                        response: Response<ProdukResponsePost>
                    ) {
                        Log.d("EditProduk", response.body().toString())

                        val namaProduk = response.body()!!.data.produk.nama

                        Toast.makeText(context, "Produk "+ namaProduk + " berhasil diedit", Toast.LENGTH_SHORT)
                            .show()

                        findNavController().navigate(R.id.produkFragment)

                    }

                    override fun onFailure(call: Call<ProdukResponsePost>, t: Throwable) {
                        Log.e("EditProdukError", t.toString())
                    }

                })
            }
            else {
                api.tambahProduk(
                    token.toString(),
                    admin_id.toString().toInt(),
                    txtFormTambahNamaProduk.text.toString(),
                    txtFormTambahHargaProduk.text.toString().toInt(),
                    txtFormTambahStokProduk.text.toString().toInt()
                ).enqueue(object :
                    Callback<ProdukResponsePost> {
                    override fun onResponse(
                        call: Call<ProdukResponsePost>,
                        response: Response<ProdukResponsePost>
                    ) {
                        Log.d("TambahProduk", response.body().toString())
                        Toast.makeText(context, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT)
                            .show()

                        findNavController().navigate(R.id.produkFragment)

                    }

                    override fun onFailure(call: Call<ProdukResponsePost>, t: Throwable) {
                        Log.e("TambahProdukError", t.toString())
                    }

                })
            }

        }

        return view
    }

}