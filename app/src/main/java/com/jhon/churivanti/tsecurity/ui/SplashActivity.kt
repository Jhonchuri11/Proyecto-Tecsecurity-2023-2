package com.jhon.churivanti.tsecurity.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(this, "Presentando...", Toast.LENGTH_SHORT).show()
                mainActi()
            })
    }

    private fun mainActi() {
        val intent = Intent(this, IntroActivity::class.java)
        //val intent = Intent(this, InitActivity::class.java)
        startActivity(intent)
        finish()
    }

}