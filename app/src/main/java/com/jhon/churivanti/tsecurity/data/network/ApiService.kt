package com.jhon.churivanti.tsecurity.data.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    // Realizamos un solicitud GET
    @GET("/v2/directions/driving-car")
    // Usando corrutinas suspend
    suspend fun getRoute(
        @Query("api_key") key:String,
        @Query("start", encoded = true) start:String,
        @Query("end", encoded = true) end:String
    ):Response<RouteResponse>

    @FormUrlEncoded
    @POST("register/")
     fun createUser(
        @Field("name") name:String,
        @Field("email") email:String,
        @Field("username") username:String,
        @Field("password") password: String
    ): Call<UserResponse>
}