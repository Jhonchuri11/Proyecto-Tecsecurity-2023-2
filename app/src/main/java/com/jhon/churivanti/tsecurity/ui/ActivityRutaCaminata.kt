package com.jhon.churivanti.tsecurity.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jhon.churivanti.tsecurity.R
import com.jhon.churivanti.tsecurity.data.network.ApiService
import com.jhon.churivanti.tsecurity.data.network.RetrofitClient
import com.jhon.churivanti.tsecurity.data.network.RouteResponse
import com.jhon.churivanti.tsecurity.databinding.ActivityRutaCaminataBinding
import com.jhon.churivanti.tsecurity.ui.view.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActivityRutaCaminata : BaseActivity<ActivityRutaCaminataBinding>(ActivityRutaCaminataBinding::inflate),
    OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMapRutaCaminata) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Boton para iniciar el recorrido de la ruta
        binding.btnCancelar.setOnClickListener {
            onBackPressed()
        }

        // Boton de regreso
        binding.iconoVolver.setOnClickListener {
           onBackPressed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        try {
            val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success)
                Toast.makeText(this, "Load map style failed",
                    Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, ""+e.message,
                Toast.LENGTH_LONG).show()
        }

        val userlocation: LatLng? = intent.getParcelableExtra("userlocation")


        if (userlocation != null) {
            moveCamera(userlocation, "Mi ubicación")

            val schoolLocation = LatLng(-12.04531023818017, -76.95263216301089)
            showRoute(userlocation, schoolLocation)

            //val startMarker = MarkerOptions().position(userlocation)
            //map.addMarker(startMarker)

            // Icono de destino
            val icono = R.drawable.finalruta
            val iconBitmap = BitmapFactory.decodeResource(resources, icono)

            // Ajustamos el ancho y tamaño del icono
            val width = 50 //  Ancho
            val height = 50 // altura
            val scaledBitmap = Bitmap.createScaledBitmap(iconBitmap, width, height, false)

            val endMarker = MarkerOptions()
                .position(schoolLocation)
                .title("Destino")
                .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                .anchor(0.5f, 0.5f)
            map.addMarker(endMarker)

            adjustCameraForRoute(userlocation, schoolLocation)
        } else {
            Toast.makeText(this, "No se pudo obtener la ubicación del usuario", Toast.LENGTH_SHORT).show()
        }

        mostrandoIncidentesReportados()
    }

    private fun showRoute(userstart: LatLng, colegioend: LatLng) {

        val start = "${userstart.longitude}, ${userstart.latitude}"
        val end = "${colegioend.longitude}, ${colegioend.latitude}"

        Log.i("JWA", start)
        Log.i("JWA", end)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = getRetrofit().create(ApiService::class.java)
                    .getRoute("5b3ce3597851110001cf6248b76dc09373cd483c81252f3a4b390023", start, end)

                if (call.isSuccessful) {
                    drawRoute(userstart, colegioend, call.body())
                } else {
                    Log.i("JWA", "Request failed with code ${call.code()}")
                }
            } catch (e: Exception) {
                Log.e("JWA", "Error: ${e.message}")
            }
        }

    }

    // Funcion que traza la ruta en el mapa de inicio a fin : destino
    @SuppressLint("MissingInflatedId")
    private fun drawRoute(startLatLg: LatLng, endLatLg: LatLng, routeResponse: RouteResponse?) {

        val polylineOptions = PolylineOptions()

        routeResponse?.features?.firstOrNull()?.geometry?.coordinates?.forEach { coordinate ->
            // La latitud es la primera coordenada y la longitud es la segunda
            val point = LatLng(coordinate[1], coordinate[0])
            polylineOptions.add(point)
        }

        if (polylineOptions.points.isNotEmpty()) {

            this.runOnUiThread() {
                // Añade la polyline al mapa solo si hay puntos en la ruta
                val poly = map.addPolyline(
                    polylineOptions
                        .color(ContextCompat.getColor(this, R.color.white))
                        .width(resources.getDimension(R.dimen.routeWidth))
                        .startCap(RoundCap())
                        .endCap(RoundCap())
                        .jointType(JointType.ROUND)
                )
            }

        } else {
            // Manejar el caso donde no hay puntos en la ruta (podría ser un escenario de error)
            Log.e("JWA", "No hay puntos en la ruta.")
        }
    }
    private fun mostrandoIncidentesReportados() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.obtenerlistaIncidenteAprobado()
                }
                if (response.isSuccessful) {
                    val incidente = response.body()

                    incidente?.let {
                        for ( incidenteA in it) {
                            val ubicacion = LatLng(incidenteA.latitud.toDouble(), incidenteA.longitud.toDouble())

                            // Mostramos el icono segun el nivel de peligro
                            val icono = when (incidenteA.nivelPeligro) {
                                "Bajo" -> R.drawable.bajo
                                "Moderado" -> R.drawable.moderado
                                "Alto" -> R.drawable.alto
                                else -> R.drawable.ic_facebook
                            }

                            val iconBitmap = BitmapFactory.decodeResource(resources, icono)

                            // Ajustamos el ancho y tamaño del icono
                            val width = 50 //  Ancho
                            val height = 50 // altura
                            val scaledBitmap = Bitmap.createScaledBitmap(iconBitmap, width, height, false)

                            // Si todo esta correcto procedemos mostrar el marcador
                            val marker = map.addMarker(
                                MarkerOptions()
                                    .position(ubicacion)
                                    .title(incidenteA.descripcion)
                                    .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                                    .anchor(0.5f, 0.5f)
                            )

                            // Configurar el listener de clic para el marcador
                            map.setOnMarkerClickListener { clickedMarker ->
                                if (clickedMarker == marker) {
                                    // Si el marcador clickeado es el mismo que el que agregamos, mostramos el diálogo
                                    mostrarDialogoInformacion(incidenteA.descripcion, incidenteA.nivelPeligro)
                                    true
                                } else {
                                    false
                                }
                            }

                        }
                    }
                } else {
                    Log.e("Error", "Error al obtener la ubicación: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Error", "Error al obtener la ubicación: ${e.message}")
            }
        }
    }

    private fun mostrarDialogoInformacion(descripcion: String, nivelPeligro: String) {
        // Crea y muestra el diálogo con la información del incidente
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Información del Incidente reportado")
        dialogBuilder.setMessage("Descripción: ${descripcion}\nNivel de Peligro: ${nivelPeligro} \nDetalle: \nA: Alto \nB: Bajo \nM: Moderado")

        dialogBuilder.setPositiveButton("Cerrar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun moveCamera(latLng: LatLng, title: String) {
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(17.0f)
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        // Icono de inicio
        val icono = R.drawable.inicio
        val iconBitmap = BitmapFactory.decodeResource(resources, icono)

        // Ajustamos el ancho y tamaño del icono
        val width = 50 //  Ancho
        val height = 50 // altura
        val scaledBitmap = Bitmap.createScaledBitmap(iconBitmap, width, height, false)

        map.addMarker(
            MarkerOptions()
                .position(latLng).
                title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                .anchor(0.5f, 0.5f))
    }

    // Funcion que mueve la cámara de ubicacion actual a ubicacion de destino
    private fun adjustCameraForRoute(userlocation: LatLng, colegioend: LatLng) {
        val builder = LatLngBounds.Builder()
        builder.include(userlocation)
        builder.include(colegioend)

        val bounds = builder.build()

        val padding = 120 // valores
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)

        map.animateCamera(cameraUpdate)
    }
}