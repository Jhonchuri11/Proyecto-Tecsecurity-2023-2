package com.jhon.churivanti.tsecurity.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhon.churivanti.tsecurity.data.network.Comentario
import com.jhon.churivanti.tsecurity.data.network.ResponseComentario
import com.jhon.churivanti.tsecurity.data.network.RetrofitClient
import com.jhon.churivanti.tsecurity.util.SharedPreferenceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComentarioViewModel(private val context: Context) : ViewModel() {

    // Para el register con firebase
    private lateinit var auth: FirebaseAuth
    // Para el guardado de dato local
    private lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    val fieldsPasswordIquals = MutableLiveData<Boolean>()
    val gooRegisterComentario = MutableLiveData<Boolean>()
    val errorRegister = MutableLiveData<Boolean>()

    fun onCreate() {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth
    }


    fun validateData(comentario: String ) {

        if (comentario.isEmpty()) {
            fieldsPasswordIquals.postValue(true)
            return
        } else {
            apiRegisterComentario(comentario)
        }

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

                            gooRegisterComentario.postValue(true)

                            Log.d("Register", "Respuesta exitosa: $message")
                        } else {
                            Log.d("Register", "Respuesta exitosa: $userId")
                            errorRegister.postValue(true)
                            Log.d("Register", "Error la respuesta del servidor: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<ResponseComentario>, t: Throwable) {
                        Log.e("Register", "Error en la llamada a la API de Django: ${t.message}", t)
                    }
                })
        }
    }