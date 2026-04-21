// data/Manufacturer.kt
package com.example.list_temp.data

import java.util.UUID

data class Manufacturer(
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var bikeTypeID: UUID? = null // Связь с типом велосипеда
)