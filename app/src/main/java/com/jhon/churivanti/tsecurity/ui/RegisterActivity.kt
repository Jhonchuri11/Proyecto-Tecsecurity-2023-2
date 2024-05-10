package com.jhon.churivanti.tsecurity.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.jhon.churivanti.tsecurity.data.network.RegisterUserResponse
import com.jhon.churivanti.tsecurity.databinding.ActivityRegisterBinding
import com.jhon.churivanti.tsecurity.ui.viewmodel.RegisterViewModel

class RegisterActivity : BaseActivity<ActivityRegisterBinding>(ActivityRegisterBinding::inflate) {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si el user opta por iniciar sesión
        binding.tvHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Boton para registro

        binding.btnRegister.setOnClickListener {
            startRegisterUser()
        }

        userRegistrado()

    }

    // Esta funcion califica si el usuario ya inicio sesion no pueda acceder a registro antes
    // debe cerrar sesion para poder usarlo el formulario
    private fun userRegistrado() {
        // Esta funcion permite
        if (FirebaseAuth.getInstance().currentUser == null) {
            // Llamando e inicializando la funcion de observacion de datas
            observeViewModel()
        } else {
            val intent = Intent(this, InitActivity::class.java)
            startActivity(intent)
            finish()
        }
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
            Toast.makeText(this, "La contraseña debe tener unos de estos carácteres ‐@#\$%^&+", Toast.LENGTH_SHORT).show()
        }

        // Validando dos campos de contraseñas, si no coinciden se procede a enviar el mensaje
        registerViewModel.fieldsPasswordIquals.observe(this) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        }

        // Si el registro es exitoso se procede a redirigir al usuer al activity destinado
        registerViewModel.goSuccessRegister.observe(this) {

            binding.animationView.playAnimation()
            // Mostrandi animation
            binding.animationView.visibility = View.VISIBLE
            // Muestra aldente de todos
            binding.animationView.bringToFront()

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, InitActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }, 2000)
        }


        // Si hay un error en el registro se procede a enviar un error
        registerViewModel.registerFailds.observe(this) {
            Toast.makeText(
                baseContext,
                "Hubo un error al registrarse, vuelva a intentar!",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    // Funcion que recoge la data en el xml, ingresada por el usuario

    private fun startRegisterUser() {
        registerViewModel.validateData(
            binding.etFullname.text.toString(),
            binding.etEmail.text.toString(),
            binding.etUsername.text.toString(),
            binding.etPassword.text.toString(),
            binding.etConfirmPassword.text.toString()
        )
    }
}