package com.jhon.churivanti.tsecurity.ui.view

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.jhon.churivanti.tsecurity.databinding.FragmentHomeBinding
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.jhon.churivanti.tsecurity.R
import com.jhon.churivanti.tsecurity.data.network.RetrofitClient
import com.jhon.churivanti.tsecurity.ui.ActivityRutaCaminata
import com.jhon.churivanti.tsecurity.ui.FormularioRegistroIncidente
import com.jhon.churivanti.tsecurity.ui.RutaActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.Exception


class HomeFragment: BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), OnMapReadyCallback  {

    private lateinit var map: GoogleMap

    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    lateinit var fusedLocationProviderClient:FusedLocationProviderClient

    override fun onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)


        createFragment()
        init()

        binding.fab.setOnClickListener {
            showBottomDialog()
        }
    }

    private fun init() {
        locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(1000)
        locationRequest.setSmallestDisplacement(10f)
        locationRequest.interval = 2000

        locationCallback = object : LocationCallback() {
            // ctrl + o
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                val newPos = LatLng(p0!!.lastLocation.latitude, p0!!.lastLocation.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos,18f))
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }


    private fun createFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        // Buscando los permisos necesarios en la ubicacion del user
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object :PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    map.isMyLocationEnabled = true
                    map.uiSettings.isMyLocationButtonEnabled = true
                    map.setOnMyLocationButtonClickListener {
                        fusedLocationProviderClient.lastLocation
                            .addOnFailureListener { e->
                                Snackbar.make(requireView(), e.message!!,
                                    Snackbar.LENGTH_LONG).show()
                            }
                            .addOnSuccessListener { location ->
                                val userLatLng = LatLng(location.latitude, location.longitude)
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 18f))

                                // llamando a funcion de inicio de ruta

                            }
                        true
                    }
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Snackbar.make(requireView(), p0!!.permissionName+"needed for run app",
                        Snackbar.LENGTH_LONG).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

            })
            .check()


        try {
            val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            if (!success)
                Snackbar.make(requireView(), "Load map style failed",
                    Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Snackbar.make(requireView(), ""+e.message,
                Snackbar.LENGTH_LONG).show()
        }

        // Icono de calle peligrosa
        icon()
        // Para mostrar un formualario si el user quiere enviar en reporte
        clickComentario()
        // Mostramos los reportes del user
        mostrandoIncidentesReportados()

    }

    private fun clickComentario() {
        map.setOnMapClickListener { latLgn ->
            confirmedIncidente(latLgn)
        }
    }

    private fun confirmedIncidente(latLng: LatLng) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmación para registrar un incidente")
            .setMessage("Al hacer click en esta ubicación, confirma que ocurrió" +
                    " un incidente y quiere reportarlo. ¿Confirma con un si para procederlo a registrar los datos?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Si confirmo") { _, _ ->
                mostrarFormulario(latLng)
            }
            .setNegativeButton("Cancelar ahora") {dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun mostrarFormulario(latLng: LatLng) {
        val intent = Intent(requireContext(), FormularioRegistroIncidente::class.java)
        intent.putExtra("latitud", latLng.latitude)
        intent.putExtra("longitud", latLng.longitude)
        startActivity(intent)
    }


    // Recogiendo data de base de datos
    // Utilizando retrofit a traves de la interface retrofit cliente
    // De esta manera usando corrutinas, a traves de ello obteniendo la ubicacion
    // Despues los demas datos para mostrarlos en el mapa
    private fun icon() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                   RetrofitClient.instance.obtenerRutaPeligrosa()
                }

                if (response.isSuccessful) {
                    val callepeli = response.body()

                    callepeli?.let {
                        for (calle in it) {
                            val ubicacion = LatLng(calle.latitud.toDouble(), calle.longitud.toDouble())

                            // Seleccionar el recurso de icono según el nivel de peligro
                            val iconResource = when (calle.nivelPeligro) {
                                1 -> R.drawable.ic_facebook
                                5 -> R.drawable.ic_facebook
                                else -> R.drawable.illustration_started
                            }

                            // Cargando el icon
                            val iconBitmap = BitmapFactory.decodeResource(resources, iconResource)

                            // Ajustando el ancho y tamaño del icono
                            val width = 50 // ancho
                            val height = 50 // altura
                            val scaledBitmap = Bitmap.createScaledBitmap(iconBitmap, width, height, false)

                            // Mostramos el marcador en el mapa
                            map.addMarker(
                                MarkerOptions()
                                    .position(ubicacion)
                                    .title(calle.nombre)
                                    .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                                    .anchor(0.5f, 0.5f)
                            )
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

                            // Configurar el listener de clic para todos los marcadores
                            map.setOnMarkerClickListener { clickedMarker ->
                                // Recuperamos la lista de incidentes para encontrar lo que le corresponde al marcador que fue clickeado
                                val incidenteClicado = it.find { it.descripcion == clickedMarker.title }
                                if (incidenteClicado != null) {
                                    // Si se encuentra icono de incidente se muetra el dialogo
                                    mostrarDialogoInformacion(incidenteClicado.descripcion, incidenteClicado.nivelPeligro)
                                }
                                true
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
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Información del Incidente reportado")
        dialogBuilder.setMessage("Descripción: ${descripcion}\nNivel de Peligro: ${nivelPeligro} \nDetalle: \nA: Alto \nB: Bajo \nM: Moderado")

        dialogBuilder.setPositiveButton("Cerrar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    // Funcion que abre un semimodal con opciones para el user
    private fun showBottomDialog() {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)

        val botonRutaTecMovil = dialog.findViewById<LinearLayout>(R.id.rutaTecsupmovil)
        val botonRutaTecCaminata = dialog.findViewById<LinearLayout>(R.id.rutaTecsupie)

        val cancelarButton = dialog.findViewById<ImageView>(R.id.cancelButton)
        botonRutaTecMovil.setOnClickListener {
            dialog.dismiss()

            obtenerUbicacionActual { userLocation ->
                if (userLocation != null) {
                    // Llama a la función para iniciar la navegación y pasa la ubicación actual
                    // iniciarNavegacion(userLocation)
                    val intent = Intent(requireContext(), RutaActivity::class.java)
                    intent.putExtra("userlocation", userLocation)
                    startActivity(intent)
                } else {
                    Snackbar.make(requireView(), "No se pudo obtener la ubicación del usuario", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        botonRutaTecCaminata.setOnClickListener {
            dialog.dismiss()
            obtenerUbicacionActual { userLocation ->
                if (userLocation != null) {
                    val intent = Intent(requireContext(), ActivityRutaCaminata::class.java)
                    intent.putExtra("userlocation", userLocation)
                    startActivity(intent)
                } else {
                    Snackbar.make(requireView(), "No se pudo obtener la ubicación del usuario", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        cancelarButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

    }

    private fun obtenerUbicacionActual(callback: (LatLng?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    // Verificar si la ubicación es válida y llamar al callback
                    val userLocation = if (location != null) LatLng(location.latitude, location.longitude) else null
                    callback(userLocation)
                }
                .addOnFailureListener { exception ->
                    // Manejar fallos al obtener la ubicación
                    Log.e("Error", "Error al obtener la ubicación: ${exception.message}")
                    callback(null)
                }
        } else {
            // Manejar el caso cuando no se tienen los permisos necesarios
            Log.e("Error", "Permisos de ubicación no otorgados.")
            callback(null)
        }
    }

    // funcion que permite mostrar la ruta a
   /* private fun iniciarNavegacion(userLatLng: LatLng) {
        // Verifica si se tiene la ubicación actual del usuario y luego inicia la navegación

        // -12.04531023818017, -76.95263216301089
        if (userLatLng != null) {
            val schoolLocation = LatLng(-12.04531023818017, -76.95263216301089)
            showRoute(userLatLng, schoolLocation)
        } else {
            Snackbar.make(requireView(), "User location not available", Snackbar.LENGTH_SHORT).show()
        }
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

    private fun drawRoute(startLatLg: LatLng, endLatLg: LatLng, routeResponse: RouteResponse?) {
        val polylineOptions = PolylineOptions()

        routeResponse?.features?.firstOrNull()?.geometry?.coordinates?.forEach { coordinate ->
            // La latitud es la primera coordenada y la longitud es la segunda
            val point = LatLng(coordinate[1], coordinate[0])
            polylineOptions.add(point)
        }

        if (polylineOptions.points.isNotEmpty()) {

            requireActivity().runOnUiThread() {
                // Añade la polyline al mapa solo si hay puntos en la ruta
                val poly = map.addPolyline(polylineOptions
                    .color(ContextCompat.getColor(requireContext(), R.color.green))
                    .width(resources.getDimension(R.dimen.routeWidth))
                    .startCap(RoundCap())
                    .endCap(RoundCap())
                    .jointType(JointType.ROUND)
                )

                // Mostrar la interfaz de usuario
                val bottomSheetView = layoutInflater.inflate(R.layout.inforuta, null)
                val tvRouteInfo: TextView = bottomSheetView.findViewById(R.id.tvRouteInfo)
                val btnLater: Button = bottomSheetView.findViewById(R.id.btnLater)
                val btnNow: Button = bottomSheetView.findViewById(R.id.btnNow)
                // Investigar mas de dar diseño a ruta

                // Mostrar la interfaz de usuario
                val bottomSheetDialog = BottomSheetDialog(requireContext())
                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetDialog.show()
            }

        } else {
            // Manejar el caso donde no hay puntos en la ruta (podría ser un escenario de error)
            Log.e("JWA", "No hay puntos en la ruta.")
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    */
}
