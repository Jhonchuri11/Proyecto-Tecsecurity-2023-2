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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhon.churivanti.tsecurity.databinding.FragmentPerfilBinding
import com.jhon.churivanti.tsecurity.ui.MainActivity
import com.jhon.churivanti.tsecurity.ui.RecuperarActivity
import com.jhon.churivanti.tsecurity.ui.configurarCuenta
import com.jhon.churivanti.tsecurity.ui.viewmodel.LoginViewModel
import com.jhon.churivanti.tsecurity.util.SharedPreferenceUtil

class PerfilFragment : BaseFragment<FragmentPerfilBinding>(FragmentPerfilBinding::inflate) {

    private lateinit var firebaseAuth: FirebaseAuth
    // Para el guardado de dato local
    private lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializando sharedpreferece
        sharedPreferenceUtil = SharedPreferenceUtil().also {
            it.setSharedPreference(requireContext())
        }
        // Configuracion de boton para cerrar sesion
        binding.btnLogout.setOnClickListener {
            mensesConfirmLogout()
        }

        // Boton de configurar cuenta
        binding.buttonCuenta.setOnClickListener {
            configPassword()
        }

        // Boton de infromacion personal
        binding.buttonPersonal.setOnClickListener {

        }
        // Inicializar FirebaseAuth
        firebaseAuth = Firebase.auth
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null && firebaseUser.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }) {
            //  Si el user inicia sesion o este autenticado con Google
            val userName = firebaseUser.displayName
            val userEmail = firebaseUser.email ?: ""

            if (userName != null) {
                // Lo mostramos el nombre y el email obtenidos de Firebase
                binding.UserName.text = userName
                binding.userEmail.text = userEmail
            } else {
                // Si no hay nombre asociado a la cuenta de Google, solo mostramos el email
                binding.UserName.text = userEmail
                binding.userEmail.text = userEmail
            }
        } else {
            // Si el user no inicia sesion con Google, se muestr la información guardada desde SharedPreferences
            val user = sharedPreferenceUtil.getUser()
            binding.UserName.text = user?.nombre ?: ""
            binding.userEmail.text = user?.email ?: ""
        }

    }

    private fun configPassword() {
        val intent = Intent(requireContext(), RecuperarActivity::class.java)
        startActivity(intent)
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
        // Cerramos actividad actual
        requireActivity().finish()
        // Iniciamos nueva actividad
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
}