package com.jhon.churivanti.tsecurity.ui.viewmodel

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhon.churivanti.tsecurity.data.network.RetrofitClient
import com.jhon.churivanti.tsecurity.data.network.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RegisterViewModel(private val context: Context) : ViewModel() {

    private lateinit var auth: FirebaseAuth

    // Variables para verificacion de register
    val fieldsPasswordIquals = MutableLiveData<Boolean>()
    val goSuccessRegister = MutableLiveData<Boolean>()
    val emptyFieldsError = MutableLiveData<Boolean>()
    val passwordDebil = MutableLiveData<Boolean>()
    val emailInvalido = MutableLiveData<Boolean>()
    val registerFailds = MutableLiveData<Boolean>()
    val retrofiResponsess = MutableLiveData<Boolean>()


    // Inicializando firebase para autenticacion
    fun onCreate() {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth
    }

    // Funcion que valida los campos del formulario
    fun validateData(name: String, email: String, username: String, password: String, password2: String) {

        if( name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            // Los campos son nulos
            emptyFieldsError.postValue(true)
            return
        }

        val passwordRegex = Pattern.compile("^" +
                "(?=.*[‐@#$%^&+])" +     // Tiene que tener 1 carácter especial
                ".{6,}") // Logintud de contraseña

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Emial no valido
            emailInvalido.postValue(true)

        } else if (password.isEmpty() || !passwordRegex.matcher(password).matches()){

            // Contraseña debil
            passwordDebil.postValue(true)

            // Contraseñas diferentes

        } else if (password != password2) {

            fieldsPasswordIquals.postValue(true)

            // Procede a crear una cuenta si se cumplen las anteriores condiciones
        } else {
            createAccount(name, email, username, password)
        }

    }

    // Funcion para crear cuenta del user con firebase
    private fun createAccount(name: String, email: String, username: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registro exitosos
                    val firebaseUser = auth.currentUser

                    ApiRegister(name, email, username, password)

                    goSuccessRegister.postValue(true)
                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    // Si ocurre un fallo al registro
                    registerFailds.postValue(true)
                }
            }
    }

    private fun ApiRegister(name: String, email: String, username: String, password: String) {
        RetrofitClient.instance.createUser(name, email, username, password)
            .enqueue(object : Callback<UserResponse>{
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    Log.e("JWCA", "Buena Django: ${response.message()}")
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e("JWCA", "Error en la llamada a la API de Django: ${t.message}")
                }
            })
    }
}


