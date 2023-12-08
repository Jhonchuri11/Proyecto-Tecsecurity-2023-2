package com.jhon.churivanti.tsecurity.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jhon.churivanti.tsecurity.R
import com.jhon.churivanti.tsecurity.databinding.ActivityInitBinding

class InitActivity : BaseActivity<ActivityInitBinding>(ActivityInitBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializandor de navegacion principal inferior
        val navView: BottomNavigationView = binding.navView

        // Fragment center
        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)

    }
}