package com.jhon.churivanti.tsecurity.ui.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel(private val context: Context): ViewModel() {

    private lateinit var auth: FirebaseAuth

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
                    gootSuccessLogin.postValue(true)
                } else {
                    // Si ocurre un fallo al login
                    gotFailsErrors.postValue(true)
                }
            }
    }


}