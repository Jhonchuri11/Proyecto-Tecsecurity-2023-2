package com.jhon.churivanti.tsecurity.ui


import android.os.Bundle
import com.jhon.churivanti.tsecurity.databinding.ActivityConfigurarCuentaBinding

class configurarCuenta : BaseActivity<ActivityConfigurarCuentaBinding>(ActivityConfigurarCuentaBinding::inflate) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnRegresar.setOnClickListener {
            onBackPressed()
        }


    }

}