// data/BikeModel.kt
package com.example.list_temp.data

import java.util.UUID

data class BikeModel(
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var manufacturerID: UUID? = null, // Связь с производителем
    var phone: String = "" // Номер телефона для звонка
)