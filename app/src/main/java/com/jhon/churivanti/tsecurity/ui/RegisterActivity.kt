package com.jhon.churivanti.tsecurity.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.jhon.churivanti.tsecurity.databinding.ActivityRegisterBinding
import com.jhon.churivanti.tsecurity.ui.viewmodel.RegisterViewModel

class RegisterActivity : BaseActivity<ActivityRegisterBinding>(ActivityRegisterBinding::inflate) {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Boton para registro

        binding.btnRegister.setOnClickListener {
            startRegisterUser()
        }

        // Llamando e inicializando la funcion de observacion de datas

        observeViewModel()
    }

    private fun observeViewModel() {

        // Inicializando el registerviewModel
        registerViewModel = RegisterViewModel(this)

        registerViewModel.onCreate()


        // Si el user deja los campos en blanco se procede a enviar mensaje
        registerViewModel.emptyFieldsError.observe(this) {
            Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_SHORT).show()
        }

        // Si el email no se registra se envia el mensaje
        registerViewModel.emailInvalido.observe(this) {
            Toast.makeText(this, "Ingresar un email válido", Toast.LENGTH_SHORT).show()
        }

        // SI el user ingresa un password corto
        registerViewModel.passwordDebil.observe(this) {
            Toast.makeText(this, "La contraseña es débil", Toast.LENGTH_SHORT).show()
        }

        // Validando dos campos de contraseñas, si no coinciden se procede a enviar el mensaje
        registerViewModel.fieldsPasswordIquals.observe(this) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        }

        // Si el registro es exitoso se procede a redirigir al usuer al activity destinado
        registerViewModel.goSuccessRegister.observe(this) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Si hay un error en el registro se procede a enviar un error
        registerViewModel.registerFailds.observe(this) {
            Toast.makeText(
                baseContext,
                "Error en el registro.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    // Funcion que recoge la data en el xml, ingresada por el usuario

    private fun startRegisterUser() {
        registerViewModel.validateData(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString(),
            binding.etConfirmPassword.text.toString()
        )
    }
}