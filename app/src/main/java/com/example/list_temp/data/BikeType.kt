// data/BikeType.kt
package com.example.list_temp.data

import java.util.UUID

data class BikeType(
    val id: UUID = UUID.randomUUID(),
    var name: String = ""
)