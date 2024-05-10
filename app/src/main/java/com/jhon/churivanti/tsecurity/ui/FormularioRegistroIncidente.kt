package com.jhon.churivanti.tsecurity.ui


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.jhon.churivanti.tsecurity.databinding.ActivityFormularioRegistroIncidenteBinding
import com.jhon.churivanti.tsecurity.ui.view.HomeFragment
import com.jhon.churivanti.tsecurity.ui.viewmodel.FormularioViewModel

class FormularioRegistroIncidente : BaseActivity<ActivityFormularioRegistroIncidenteBinding>(ActivityFormularioRegistroIncidenteBinding::inflate) {

    private lateinit var formularioViewModel: FormularioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnRegister.setOnClickListener {
            enviarDataIncidente()
        }

        binding.iconoVolver.setOnClickListener {
            onBackPressed()
        }
        formularioViewModel = FormularioViewModel(this)
        formularioViewModel.onCreate()

        formularioViewModel.emptyFieldsError.observe(this) {
            Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
        }
        formularioViewModel.goSuccessRegisterIncidentes.observe(this) {
            binding.animationView.playAnimation()
            // Mostrandi animation
            binding.animationView.visibility = View.VISIBLE
            // Muestra aldente de todos
            binding.animationView.bringToFront()

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, configurarCuenta::class.java)
                startActivity(intent)
                Toast.makeText(this, "Reporte enviando correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }, 2000)
        }
        formularioViewModel.errorRegister.observe(this) {
            Toast.makeText(this, "Error al registrar el incidente", Toast.LENGTH_SHORT).show()
        }

    }

    // Inicializa el registro y verifica los datos
    private fun enviarDataIncidente() {
        val latitud = intent.getDoubleExtra("latitud", 0.0)
        val longitud = intent.getDoubleExtra("longitud", 0.0)
        val descripcion = binding.editTextIncidente.text.toString()
        val tipo = binding.spinnerTipoIncidente.selectedItem.toString()
        val nivelP = binding.spinnerNivelPeligro.selectedItem.toString()
        formularioViewModel.validadeDatos(
            latitud,
            longitud,
            descripcion,
            tipo,
            nivelP
        )
    }
}