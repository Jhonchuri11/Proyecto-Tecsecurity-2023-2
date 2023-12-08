package com.jhon.churivanti.tsecurity.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.jhon.churivanti.tsecurity.R
import com.jhon.churivanti.tsecurity.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private var TAG = HomeActivity::class.java.simpleName
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // El siguiente método crea el mapa

        createFragment()
    }

    // Creaciones de funciones
    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Esta funcion es llamada si se muestra el mapa
    override fun onMapReady(googleMap: GoogleMap) {
        // En este punto para poder hacer que nuestro diseño de mapa implementado
        // anteriormente se pueda usar y visuailiar
        // Primero se debe inicializar e instanciar el componenete map
        map = googleMap

        // Despues de instanciar llamaomos a nuestra funcion StyleMap para poder pasarlo instanciadp
        // Con esto lograriamos ver el mapa con el stylo predeterminado
        setStyleMap(map)

        // Si todo esta correcto, se muestra la localizacion del user en el mapa tiempo actual
        if (isLocationPermisionGranted()) {
            map.isMyLocationEnabled = true
            showCurrentLocation()
        } else {
            requestLocationPermmision()
        }
    }

    // Funcion con mapa diseñada
    private  fun setStyleMap(googleMap: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Error en style", e)
        }
    }

    // Metodo para saber si el permiso fue concedido por el user
    private fun isLocationPermisionGranted() =
        ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED



    // Funcion que pide permisos para maps

    private fun requestLocationPermmision() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Se pidio  los permisos pero rechazó
            Toast.makeText(this, "Ve ajustes y acepta el permiso", Toast.LENGTH_SHORT).show()

        } else {
            // Primer vez que se pide los permisos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

        REQUEST_CODE_LOCATION -> if(grantResults.isEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            Toast.makeText(this, "Para activar la localizacion, ve ajustes y acepta el permiso", Toast.LENGTH_SHORT).show()
        }
            else -> {}
        }
    }

    // Funcion que comprueba si los permisos siguen activos

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermisionGranted()) {
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localizacion, ve ajustes y acepta el permiso", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para mostrar la ubicación actual
    private fun showCurrentLocation() {
        // Obtener ubicación actual conocida del usuario
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
    }

}