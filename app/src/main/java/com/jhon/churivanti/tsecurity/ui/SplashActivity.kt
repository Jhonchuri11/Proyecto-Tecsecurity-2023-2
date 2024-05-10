package com.jhon.churivanti.tsecurity.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe({
                checkFirebaseAuthentication()
            })
    }
    private fun checkFirebaseAuthentication() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            startActivity(Intent(this, InitActivity::class.java))
        } else {
            Toast.makeText(this, "Presentando...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, IntroActivity::class.java))
        }

        finish()
    }


}