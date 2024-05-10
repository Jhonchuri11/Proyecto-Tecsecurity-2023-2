package com.jhon.churivanti.tsecurity.data.model


data class User (
    var id: String,
    var nombre: String,
    var email: String,
    var numero: String,
    var password: String
)
data class UserData (
    val user: User
)