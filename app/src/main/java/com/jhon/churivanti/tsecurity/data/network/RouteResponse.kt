package com.jhon.churivanti.tsecurity.data.network

import com.google.gson.annotations.SerializedName
import com.jhon.churivanti.tsecurity.data.model.User
import com.jhon.churivanti.tsecurity.data.model.UserData

data class RouteResponse (@SerializedName("features") val features:List<Feature>)
data class Feature(@SerializedName("geometry") val geometry:Geometry)

data class Geometry(@SerializedName("coordinates") val coordinates:List<List<Double>>)

// Lo que responde el api para el registro del cliente
data class RegisterUserResponse(
    @SerializedName("message") val message: String,
    @SerializedName("cliente_data") val cliente_data: User)

data class LoginResponse(
    @SerializedName("success") val success:Boolean,
    @SerializedName("data") val data: UserData,
    @SerializedName("jwt") val jwt: String,
    @SerializedName("message") val message: String)

data class calleResponse(

    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("latitud") val latitud: String,
    @SerializedName("longitud") val longitud: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("nivelPeligro") val nivelPeligro: Int
)

// Respuesta a la llamda de lista de incidentes
data class incidenteResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("idusuario") val idusuario: String,
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("tipoIncidente") val tipoIncidente:String,
    @SerializedName("nivelPeligro") val nivelPeligro: String
)
data class IncidenteResponseRegister(
    @SerializedName("id") val id: String,
    @SerializedName("idusuario") val idusuario: String,
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("tipoIncidente") val tipoIncidente: String,
    @SerializedName("nivelPeligro") val nivelPeligro: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("fechaCreacion") val fechaCreacion: String
)

data class Comentario(
    val idcliente: String,
    val comentario: String,
    val fechaCreacion: String
)

data class ResponseComentario(
    @SerializedName("idcliente") val idcliente: Int,
    @SerializedName("comentario") val comentario: String,
    @SerializedName("fechaCreacion") val fechaCreacion: String
)
