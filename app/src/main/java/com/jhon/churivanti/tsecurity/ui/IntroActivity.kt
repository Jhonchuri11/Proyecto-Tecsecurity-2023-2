package com.jhon.churivanti.tsecurity.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jhon.churivanti.tsecurity.R
import com.jhon.churivanti.tsecurity.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity<ActivityIntroBinding>(ActivityIntroBinding::inflate){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.buttonIntro.setOnClickListener {
            mainActivityIntro()
        }
    }

    // Funcion que permite redirigir al user al main Activity para el register o login
    private fun mainActivityIntro() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
