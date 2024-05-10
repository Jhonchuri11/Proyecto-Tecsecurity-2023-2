package com.jhon.churivanti.tsecurity.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhon.churivanti.tsecurity.data.network.IncidenteResponseRegister
import com.jhon.churivanti.tsecurity.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormularioViewModel(private val context: Context) : ViewModel() {

    // Para el register con firebase
    private lateinit var auth: FirebaseAuth

    val goSuccessRegisterIncidentes = MutableLiveData<Boolean>()
    val emptyFieldsError = MutableLiveData<Boolean>()
    val errorRegister = MutableLiveData<Boolean>()

    // Inicializando firebase para autenticacion
    fun onCreate() {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth


    }

    fun validadeDatos(latitud: Double, longitud: Double ,descripcion: String, tipo: String, nivelPeligro: String) {

        if (descripcion.isEmpty() || tipo.isEmpty() || nivelPeligro.isEmpty()) {
            emptyFieldsError.postValue(true)
            return
        } else {

            createIncidente(latitud, longitud,descripcion, tipo, nivelPeligro)
        }
    }

    fun createIncidente(latitud: Double, longitud: Double, descripcion: String, tipo: String, nivelPeligro: String) {

        val firebaseUser = auth.currentUser

        val userId = firebaseUser?.uid.toString()

        RetrofitClient.instance.registerIncidente(userId, latitud, longitud, descripcion, tipo, nivelPeligro, "Aprobado")
            .enqueue(object : Callback<IncidenteResponseRegister> {
                override fun onResponse(
                    call: Call<IncidenteResponseRegister>,
                    response: Response<IncidenteResponseRegister>
                ) {
                    if (response.isSuccessful) {

                        response.body()

                        goSuccessRegisterIncidentes.postValue(true)

                        Log.d("Register", "Respuesta exitosa: $latitud")

                    } else {

                        errorRegister.postValue(true)

                        Log.d("Register", "Error la respuesta del servidor: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<IncidenteResponseRegister>, t: Throwable) {

                    Log.e("Register", "Error en la llamada a la API de Django: ${t.message}", t)

                }
            })
    }
}