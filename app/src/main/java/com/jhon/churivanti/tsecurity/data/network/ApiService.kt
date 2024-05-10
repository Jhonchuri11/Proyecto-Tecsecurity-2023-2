package com.jhon.churivanti.tsecurity.data.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    // Realizamos un solicitud GET para el recorrido de ruta a pie
    @GET("/v2/directions/foot-hiking")
    // Usando corrutinas suspend
    suspend fun getRoute(
        @Query("api_key") key:String,
        @Query("start", encoded = true) start:String,
        @Query("end", encoded = true) end:String
    ):Response<RouteResponse>

    // Realizamos una solicitud para el recorrido de ruta movil
    @GET("/v2/directions/driving-car")
    suspend fun getRouteMovil(
        @Query("api_key") key: String,
        @Query("start", encoded = true) start: String,
        @Query("end", encoded = true) end: String
    ):Response<RouteResponse>

    // No usado
    @GET("callePeligrosaLista/")
    suspend fun obtenerRutaPeligrosa(
    ): Response<List<calleResponse>>

    @FormUrlEncoded
    @POST("registerUser/")
    //@POST("registerUser/")
     fun createUser(
        @Field("id") id: String,
        @Field("nombre") nombre:String,
        @Field("email") email:String,
        @Field("numero") numero:String,
        @Field("password") password: String
    ): Call<RegisterUserResponse>

     @FormUrlEncoded
     @POST("loginUser/")
     //@POST("loginUser/")
     fun loginUser(
         @Field("email") email: String,
         @Field("password") password: String
     ): Call<LoginResponse>

     // Para enviar registro de incidente
     @FormUrlEncoded
     @POST("incidente/")
     fun registerIncidente(
         @Field("idusuario") idusuario: String,
         @Field("latitud") latitud: Double,
         @Field("longitud") longitud: Double,
         @Field("descripcion") descripcion: String,
         @Field("tipoIncidente") tipoIncidente: String,
         @Field("nivelPeligro") nivelPeligro: String,
         @Field("estado") estado: String

     ): Call<IncidenteResponseRegister>

     // Recogiendo todos los incidentes
     @GET("listaInciAprobado/")
     suspend fun obtenerlistaIncidenteAprobado(
     ): Response<List<incidenteResponse>>

     // Para mostrar los comentarios de los users
     @GET("listcomentario/")
     suspend fun obtenerComentarios(): List<Comentario>

     // Para crear un comentario de un user
     @FormUrlEncoded
     @POST("comentarios/")
     fun registerComentario(
         @Field("idcliente") idcliente: String,
         @Field("comentario") comentario: String
     ): Call<ResponseComentario>

}