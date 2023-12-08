package com.jhon.churivanti.tsecurity.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RecuperarPasswordViewModel(private val context: Context): ViewModel() {

    private lateinit var auth: FirebaseAuth

    val fieldBlant = MutableLiveData<Boolean>()
    val gooSuccessSendEmail = MutableLiveData<Boolean>()
    val faildErrrosData = MutableLiveData<Boolean>()


    // Inicializamos Firebase
    fun onCreate() {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth
    }

    // Validando el campo a ingresar correo electronico valido
    fun validateData(email: String) {
        // Si esta vacio el campo a enviar se muestra el mensaje
        if (email.isEmpty()) {
            fieldBlant.postValue(true)
            return
            // Todo lo contrario se envia el correo al user identificaado
        } else {
            sendEmail(email)
        }
    }

    private fun sendEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    gooSuccessSendEmail.postValue(true)
                } else {
                    faildErrrosData.postValue(true)
                }
            }
    }
}