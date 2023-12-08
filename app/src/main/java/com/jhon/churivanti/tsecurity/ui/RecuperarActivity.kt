package com.jhon.churivanti.tsecurity.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.jhon.churivanti.tsecurity.databinding.ActivityRecuperarBinding
import com.jhon.churivanti.tsecurity.ui.viewmodel.RecuperarPasswordViewModel

class RecuperarActivity : BaseActivity<ActivityRecuperarBinding>(ActivityRecuperarBinding::inflate) {

    private lateinit var recuperarPasswordViewModel: RecuperarPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Configuración del botón de restablecimiento de contraseña
        binding.btnResetPw.setOnClickListener {
            startSendEmail()
        }

        observeModel()
        // Configuración del enlace para volver a la pantalla de inicio de sesión
        binding.tvBackLogin.setOnClickListener {
            // Puedes agregar cualquier lógica adicional que desees antes de volver a la pantalla de inicio de sesión
            finish() // Cierra la actividad actual
        }
    }

    // Funcion que permite dar detalles ante del envio de email y despues de enviar

    private fun observeModel() {

        recuperarPasswordViewModel = RecuperarPasswordViewModel(this)
        recuperarPasswordViewModel.onCreate()

        recuperarPasswordViewModel.fieldBlant.observe(this) {
            Toast.makeText(this, "Por favor, ingresa tu dirección de correo electrónico", Toast.LENGTH_SHORT).show()
        }

        recuperarPasswordViewModel.gooSuccessSendEmail.observe(this) {

            Toast.makeText(
                this,
                "Se ha enviado un correo electrónico de restablecimiento de contraseña",
                Toast.LENGTH_SHORT
            ).show()
            // Fin de la actividad después de enviar el correo electrónico
            finish()
        }

        recuperarPasswordViewModel.faildErrrosData.observe(this) {
            Toast.makeText(
                this,
                "Error al enviar el correo electrónico de restablecimiento de contraseña",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Funcion que recoge data ingresada del user
    private fun startSendEmail() {
        recuperarPasswordViewModel.validateData(
            binding.etEmail.text.toString()
        )
    }
}