package com.jhon.churivanti.tsecurity.ui.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class VerificationViewModel(private val context: Context): ViewModel() {

    private lateinit var auth: FirebaseAuth


    // PARA verificar por email user
    val emailVerifiedUser = MutableLiveData<Boolean>()


    fun onCreate() {
        FirebaseApp.initializeApp(context)
        auth = Firebase.auth
    }

    // Funcion que permite verificar el email

    private fun sendEmailVerification() {
        val user = auth.currentUser!!
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                emailVerifiedUser.postValue(true)
            }
        }
    }


}