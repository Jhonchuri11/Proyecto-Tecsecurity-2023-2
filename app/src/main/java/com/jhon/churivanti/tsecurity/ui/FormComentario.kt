package com.jhon.churivanti.tsecurity.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.jhon.churivanti.tsecurity.R
import com.jhon.churivanti.tsecurity.databinding.ActivityFormComentarioBinding
import com.jhon.churivanti.tsecurity.ui.view.VisitFragment
import com.jhon.churivanti.tsecurity.ui.viewmodel.ComentarioViewModel

class FormComentario : BaseActivity<ActivityFormComentarioBinding>(ActivityFormComentarioBinding::inflate) {

    private lateinit var comentarioViewModel: ComentarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnRegister.setOnClickListener {
            startRegisterComentario()
        }

        comentarioViewModel = ComentarioViewModel(this)
        comentarioViewModel.onCreate()

        comentarioViewModel.fieldsPasswordIquals.observe(this) {
            Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_SHORT).show()
        }

        comentarioViewModel.gooRegisterComentario.observe(this) {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Comentario creado")
            alertDialogBuilder.setMessage("¡Comentario creado correctamente!")

            alertDialogBuilder.setPositiveButton("Enviar") { _, _ ->
                // Acciones a realizar cuando se hace clic en el botón "Aceptar" del cuadro de diálogo
                // Por ejemplo, navegar a un fragmento
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val fragment = VisitFragment()
                fragmentTransaction.replace(R.id.fab, fragment)
                fragmentTransaction.commit()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        comentarioViewModel.errorRegister.observe(this) {
            Toast.makeText(
                baseContext,
                "Hubo un error al registrar el comentario, vuelva a intentar!",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun startRegisterComentario() {
        comentarioViewModel.validateData(
            binding.editTextcomentario.text.toString()
        )
    }
}