package com.jhon.churivanti.tsecurity.ui.viewmodel

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhon.churivanti.tsecurity.data.model.User
import com.jhon.churivanti.tsecurity.data.network.RegisterUserResponse
import com.jhon.churivanti.tsecurity.data.network.RetrofitClient
import com.jhon.churivanti.tsecurity.util.SharedPreferenceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RegisterViewModel(private val context: Context) : ViewModel() {

    // Para el register con firebase
    private lateinit var auth: FirebaseAuth
    // Para el guardado de dato local
    private lateinit var sharedPreferenceUtil: SharedPreferenceUtil

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

        // Instanciando la clase sharedprederencesUtil
        sharedPreferenceUtil = SharedPreferenceUtil().also {
            it.setSharedPreference(context)
        }

    }

    // Funcion que valida los campos del formulario
    fun validateData(nomape: String, email: String, numero: String, password: String, password2: String) {

        if( nomape.isEmpty() || email.isEmpty() || numero.isEmpty() || password.isEmpty() || password2.isEmpty()) {
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
            createAccount(nomape, email, numero, password)
        }

    }

    // Funcion para crear cuenta del user con firebase
    private fun createAccount(nomape: String, email: String, numero: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registro exitosos
                    val firebaseUser = auth.currentUser
                    // Se recupera el id del user en Firebase
                    val id = firebaseUser?.uid

                    // Ingreamos los datos en el dispositivo local del user
                    val user = User(id ?:"", nomape, email, numero, password)

                    // Llamamos a la función del para registrar los datos en el api
                   apiRegister(user)

                    // Guadrsamos los datos del user en el dispositivo local del user
                    sharedPreferenceUtil.saveUser(user)

                    goSuccessRegister.postValue(true)
                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    // Si ocurre un fallo al registro
                   registerFailds.postValue(true)
                }
            }
    }

    private fun apiRegister(user: User) {
        RetrofitClient.instance.createUser(user.id,user.nombre, user.email, user.numero, user.password)
            .enqueue(object : Callback<RegisterUserResponse>{
                override fun onResponse(
                    call: Call<RegisterUserResponse>,
                    response: Response<RegisterUserResponse>) {
                    if (response.isSuccessful) {

                        val registerUserResponse = response.body()
                        val message = registerUserResponse?.message
                        Log.d("Register", "Respuesta exitosa: $message")

                    } else {
                        Log.d("Register", "Error la respuesta del servidor: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RegisterUserResponse>, t: Throwable) {
                    Log.e("Register", "Error en la llamada a la API de Django: ${t.message}", t)
                }
            })
    }
}


