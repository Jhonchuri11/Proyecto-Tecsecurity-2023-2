package com.jhon.churivanti.tsecurity.ui.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhon.churivanti.tsecurity.databinding.FragmentPerfilBinding
import com.jhon.churivanti.tsecurity.ui.MainActivity
import com.jhon.churivanti.tsecurity.ui.viewmodel.LoginViewModel

class PerfilFragment : BaseFragment<FragmentPerfilBinding>(FragmentPerfilBinding::inflate) {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuracion de boton para cerrar sesion
        binding.btnLogout.setOnClickListener {
            mensesConfirmLogout()
        }

        // Inicializar FirebaseAuth
        firebaseAuth = Firebase.auth

        binding.textView2.text = firebaseAuth.currentUser?.email ?: ""

    }

    // Funcion que muestra un mensaje de confirmacion por el user
    private fun mensesConfirmLogout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cerrar sesión")
        builder.setMessage("Está seguro de cerrar sesión?")

        builder.setPositiveButton("Si") {_, _ ->
            mostrarAnimationSussoend()
        }

        builder.setNegativeButton("No") {_, _ ->

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun mostrarAnimationSussoend() {
        binding.animationView.playAnimation()

        binding.animationView.visibility = View.VISIBLE

        binding.animationView.bringToFront()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.animationView.cancelAnimation()
            binding.animationView.visibility = View.GONE
            logout()
        }, binding.animationView.duration + 3000)
    }

    // Funcion que ejecuta cerrar sesion y redirige al user
    private fun logout() {
        firebaseAuth.signOut()
        Toast.makeText(requireContext(), "Sesión cerrada correctamente!", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
}