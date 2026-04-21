package com.example.list_temp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.list_temp.data.BikeModel

@Dao
interface BikeModelDao {
    @Query("SELECT * FROM bike_models WHERE manufacturerId = :manId ORDER BY name")
    fun getByManufacturer(manId: String): LiveData<List<BikeModel>>

    @Query("SELECT * FROM bike_models ORDER BY name")
    suspend fun getAllSync(): List<BikeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bikeModel: BikeModel)

    @Update
    suspend fun update(bikeModel: BikeModel)

    @Delete
    suspend fun delete(bikeModel: BikeModel)

    @Query("DELETE FROM bike_models")
    suspend fun deleteAll()
}