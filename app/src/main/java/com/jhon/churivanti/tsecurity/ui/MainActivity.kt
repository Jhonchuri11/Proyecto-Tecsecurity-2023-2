package com.jhon.churivanti.tsecurity.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.jhon.churivanti.tsecurity.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Registrar
        binding.btnLogin.setOnClickListener {
            startLogin()
        }

        // Login
        binding.btnRegister.setOnClickListener {
            startRegister()
        }
    }

    // Boton para crear cuenta de usuario

    private fun startRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        Log.d("RegisterActivity", "Intent started")
    }

    // Boton para iniciar sesion usuario

    private fun startLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}