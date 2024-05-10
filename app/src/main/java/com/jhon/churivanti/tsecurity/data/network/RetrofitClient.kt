package com.jhon.churivanti.tsecurity.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api-tec.onrender.com/apiTec/1.0/"

    // Esta ruta se usa solo para pruebas de base local
    //private const val BASE_URL = "http://10.200.173.222:8000/apiTec/1.0/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}