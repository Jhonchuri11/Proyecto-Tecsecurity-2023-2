package com.jhon.churivanti.tsecurity.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.jhon.churivanti.tsecurity.databinding.ActivityLoginBinding
import com.jhon.churivanti.tsecurity.ui.viewmodel.LoginViewModel

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Boton de iniciar sesion
        binding.btnLogin.setOnClickListener {
            startLoginUser()
        }

        observeViewModel()
    }

    // Funcion que recoge las condiciones y verifica si hay un fallo para mostrar lo definido
    private fun observeViewModel() {

        // Inicializando loginViewModel
        loginViewModel = LoginViewModel(this)
        loginViewModel.onCreate()

        // Si el user no ingreso ninguna data
        loginViewModel.fildsBlant.observe(this) {
            Toast.makeText(
                baseContext,
                "Completar el campos.",
                Toast.LENGTH_SHORT,).show()
        }

        // Si las credenciales cumplen se procede a enviar al user al activity home
        loginViewModel.gootSuccessLogin.observe(this) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // Si ocurre un error o los datos son incorrectos
        loginViewModel.gotFailsErrors.observe(this) {
            Toast.makeText(
                baseContext,
                "Usuario y contraseña incorrectos!",
                Toast.LENGTH_SHORT,).show()
        }
    }

    // Funcion de login que recoge data ingresado por el user
    private fun startLoginUser() {
        loginViewModel.validateDatas(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )
    }
}