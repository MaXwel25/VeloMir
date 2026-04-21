package com.example.list_temp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "bike_models",
    foreignKeys = [
        ForeignKey(
            entity = Manufacturer::class,
            parentColumns = ["id"],
            childColumns = ["manufacturerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BikeModel(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var phone: String = "",
    var manufacturerId: String = ""  // внешний ключ
)