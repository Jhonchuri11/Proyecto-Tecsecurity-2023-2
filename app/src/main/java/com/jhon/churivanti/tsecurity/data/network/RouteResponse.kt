package com.jhon.churivanti.tsecurity.data.network

import com.google.gson.annotations.SerializedName

data class RouteResponse ( @SerializedName("features") val features:List<Feature>)
data class Feature(@SerializedName("geometry") val geometry:Geometry)

data class Geometry(@SerializedName("coordinates") val coordinates:List<List<Double>>)

// Lo que responde el api para el registro del cliente
data class UserResponse(@SerializedName("message") val message: String)