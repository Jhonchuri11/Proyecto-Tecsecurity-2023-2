package com.jhon.churivanti.tsecurity.ui.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhon.churivanti.tsecurity.R
import com.jhon.churivanti.tsecurity.data.network.ResponseComentario
import com.jhon.churivanti.tsecurity.data.network.RetrofitClient
import com.jhon.churivanti.tsecurity.databinding.ActivityVisitFragmentBinding
import com.jhon.churivanti.tsecurity.ui.FormComentario
import com.jhon.churivanti.tsecurity.ui.RutaActivity
import com.jhon.churivanti.tsecurity.ui.adapter.CometarioAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisitFragment : BaseFragment<ActivityVisitFragmentBinding>(ActivityVisitFragmentBinding::inflate) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var comentarioAdapter: CometarioAdapter
    private lateinit var progressBar: ProgressBar

    // Para el register con firebase
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(requireContext())
        auth = Firebase.auth

        // Accede a la RecyclerView a través del objeto binding
        recyclerView = binding.radio

        comentarioAdapter = CometarioAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = comentarioAdapter

        // Realiza la llamada a la API
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val comentarios = RetrofitClient.instance.obtenerComentarios()
                withContext(Dispatchers.Main) {
                    comentarioAdapter.actualizarComentarios(comentarios)
                }
            } catch (e: Exception) {
                // Manejar errores
            }
        }

        binding.fab.setOnClickListener {

            showBottomDialog()
        }
        // Para recargar en la llamada a la api
        configSwipe()
    }

    private fun configSwipe() {
        binding.swipe.setColorSchemeResources(R.color.logo1, R.color.logo2, R.color.logo3)
        binding.swipe.setOnRefreshListener {

            // Se realiza la llamada a la API para obtener nuevos comentarios
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val nuevosComentarios = RetrofitClient.instance.obtenerComentarios()
                    withContext(Dispatchers.Main) {
                        comentarioAdapter.actualizarComentarios(nuevosComentarios)

                        // 2 segundos de carga
                        delay(2000)
                        // Detener la animación de actualización
                        binding.swipe.isRefreshing = false
                    }
                } catch (e: Exception) {

                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al cargar nuevos comentarios", Toast.LENGTH_SHORT).show()
                        // Detener la animación si hay un error
                        binding.swipe.isRefreshing = false
                    }
                }
            }
        }
    }
    private fun showBottomDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ingrese un Comentario")

        // Agregar un campo de entrada de texto al cuadro de diálogo
        val input = EditText(requireContext())
        builder.setView(input)


        builder.setPositiveButton("Aceptar") { dialog, which ->


            // Acciones a realizar cuando se hace clic en el botón "Aceptar"
            val comentarioIngresado = input.text.toString()
            // Puedes utilizar 'comentarioIngresado' para lo que necesites
            // Por ejemplo, puedes registrar el comentario aquí
            apiRegisterComentario(comentarioIngresado)
            Snackbar.make(requireView(), "Comentario registrado: $comentarioIngresado", Snackbar.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            // Acciones a realizar cuando se hace clic en el botón "Cancelar" (si es necesario)
        }

        val dialog = builder.create()
        dialog.show()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun apiRegisterComentario(comentario: String) {
        val firebaseUser = auth.currentUser
        val userId = firebaseUser?.uid.toString()
        RetrofitClient.instance.registerComentario(userId,comentario)

            .enqueue(object : Callback<ResponseComentario> {
                override fun onResponse(
                    call: Call<ResponseComentario>,
                    response: Response<ResponseComentario>
                ) {
                    if (response.isSuccessful) {

                        val registerComentarioo = response.body()
                        val message = registerComentarioo?.comentario

                        Snackbar.make(requireView(), "Comentario registrado: $comentario", Snackbar.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(requireContext(), "Comentario no registrado:", Toast.LENGTH_SHORT).show()
                        Log.d("Register", "Error la respuesta del servidor: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseComentario>, t: Throwable) {
                    Log.e("Register", "Error en la llamada a la API de Django: ${t.message}", t)
                }
            })
    }
}