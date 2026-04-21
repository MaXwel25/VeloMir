package com.example.list_temp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "manufacturers",
    foreignKeys = [
        ForeignKey(
            entity = BikeType::class,
            parentColumns = ["id"],
            childColumns = ["bikeTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Manufacturer(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var bikeTypeId: String = ""  // внешний ключ
)