package com.jhon.churivanti.tsecurity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.jhon.churivanti.tsecurity.R
import com.jhon.churivanti.tsecurity.data.network.ApiService
import com.jhon.churivanti.tsecurity.data.network.RouteResponse
import com.jhon.churivanti.tsecurity.databinding.ActivityMapBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapActivity : BaseActivity<ActivityMapBinding>(ActivityMapBinding::inflate), OnMapReadyCallback {

    private lateinit var map : GoogleMap
    private lateinit var btnCalcular: Button

    // Variables para la ruta inicial y final
    private  var start:String = ""
    private var end:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Llamando al boton que calcual ruta
        btnCalcular = binding.BotonCalcularRoute


        btnCalcular.setOnClickListener {
            start = ""
            end = ""
            // Ver si el mapa ha sido cargado
            if(::map.isInitialized) {
                map.setOnMapClickListener {
                    // Se comprueba si esta vacio
                    if(start.isEmpty()) {
                        start = "${it.longitude},${it.latitude}"
                        // si la segunda variable  esta vacio
                    } else if (end.isEmpty()) {
                        end = "${it.longitude},${it.latitude}"
                        createRoute()
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
    }

    private fun createRoute() {
        Log.i("JWA", start)
        Log.i("JWA", end)
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofi().create(ApiService::class.java).getRoute("5b3ce3597851110001cf6248b76dc09373cd483c81252f3a4b390023",start, end)
            if (call.isSuccessful) {
                drawRoute(call.body())
            } else {
                Log.i("JWA", "FAILD")
            }
        }
    }
    private fun drawRoute(routeResponse: RouteResponse?) {
        val polylineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
            polylineOptions.add(LatLng(it[1], it[0]))
        }
        runOnUiThread {
            val poly = map.addPolyline(polylineOptions)
        }
    }
     // Funciona que muestra la ruta

    private fun getRetrofi():Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}