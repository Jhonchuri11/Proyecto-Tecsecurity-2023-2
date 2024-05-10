package com.jhon.churivanti.tsecurity.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.util.RetainForClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhon.churivanti.tsecurity.data.network.LoginResponse
import com.jhon.churivanti.tsecurity.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val context: Context): ViewModel() {

    private lateinit var auth: FirebaseAuth

    // Para iniciar sesion con gooogle

    val fildsBlant = MutableLiveData<Boolean>()
    val gootSuccessLogin = MutableLiveData<Boolean>()
    val gotFailsErrors = MutableLiveData<Boolean>()

    // Inicializnado firebase
    fun onCreate() {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth
    }

    // Funcion que valida los campos de ingreso de data
    fun validateDatas(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            fildsBlant.postValue(true)
            return

            // Si es correcto se logea al user
        } else {
            login(email, password)
        }

    }

    // Funcion para el login del user

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    // Comprobando si correcto
                    loginRetro(email, password)
                    gootSuccessLogin.postValue(true)
                } else {
                    // Si ocurre un fallo al login
                    gotFailsErrors.postValue(true)
                }
            }
    }

    private fun loginRetro(email: String, password: String) {
        RetrofitClient.instance.loginUser(email, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()

                        val userId = loginResponse?.data?.user?.id
                        val jwtToken = loginResponse?.jwt

                        Log.d("Login", "Inicio de sesión exitoso: $userId, Token: $jwtToken")
                    } else {
                        Log.e("Login", "Error en la respuesta del servidor: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("Login", "Error en la llamada a la API: ${t.message}")
                }

            })
    }


}