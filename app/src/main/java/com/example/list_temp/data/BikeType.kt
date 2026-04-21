// data/BikeType.kt
package com.example.list_temp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "bike_types")
data class BikeType(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var name: String = ""
)