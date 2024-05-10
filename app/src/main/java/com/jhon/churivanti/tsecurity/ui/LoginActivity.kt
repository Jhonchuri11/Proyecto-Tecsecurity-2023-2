package com.jhon.churivanti.tsecurity.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jhon.churivanti.tsecurity.databinding.ActivityLoginBinding
import com.jhon.churivanti.tsecurity.ui.viewmodel.LoginViewModel

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    private val callbackManager = CallbackManager.Factory.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // instanciando firebase y recogiendo componentes del cliente para el uso de login
        // con cuenta de google
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Boton de login con Google
        binding.cardGoogle.setOnClickListener {
            googleLogin()
        }

        // Boton para login usando Facebook
        binding.Facebook.setOnClickListener {
            facebookLogin()
        }

        // Si el user opta por recuperar contraseña
        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, RecuperarActivity::class.java)
            startActivity(intent)

        }
        // Si el usuario optar por crear su cuenta
        binding.tvHaventAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Boton de iniciar sesion
        binding.btnLogin.setOnClickListener {
            startLoginUser()
        }

        verificandoSesionesUser()

    }

    private fun verificandoSesionesUser() {
        // Esta funcion permite ver si el usuario inicio sesion, si no se muestra el login y se procede a evaluar los registros
        if (FirebaseAuth.getInstance().currentUser == null) {
            observeViewModel()
            // Si el user ya tiene un inicio de sesion se procede a mostrarle lo vista principal
        } else {
            val intent = Intent(this, InitActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Funcion que permite hacer login con cuenta de facebokk
    private fun facebookLogin() {

        // Abre pantalla de autenticación nativa de facebook
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    result?.let {
                        val token = it.accessToken
                        val credential = FacebookAuthProvider.getCredential(token.token)

                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                            if (it.isSuccessful) {
                                redirectHomeUser(it.result?.user?.email ?: "")
                            } else {
                                Log.e("Facebook login", "Ocurrio un error:e")
                            }
                        }

                    }
                }

                // Si cacnela la actividad no se hace nada
                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {
                    Log.e("Facebook", "Ocurrio un error: $error")
                }
            })
    }

    private fun redirectHomeUser(email: String) {
        val intent = Intent(this, InitActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(intent)
        finish()
    }

    // Funcion que permite al usuario poder iniciar sesion con su cuenta google
    private fun googleLogin() {
        val signInClient = googleSignInClient.signInIntent
        launcher.launch(signInClient)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account : GoogleSignInAccount? = task.result
            if (account != null) {
                auth.signOut()
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, "Bienvenido usuario ${user!!.displayName}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, InitActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()
            }
        }
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
            val intent = Intent(this, InitActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Autenticación exitosa", Toast.LENGTH_SHORT).show()
            finish()
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