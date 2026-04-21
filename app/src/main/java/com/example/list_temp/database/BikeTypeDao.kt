package com.example.list_temp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.list_temp.data.BikeType

@Dao
interface BikeTypeDao {
    @Query("SELECT * FROM bike_types ORDER BY name")
    fun getAll(): LiveData<List<BikeType>>

    @Query("SELECT * FROM bike_types ORDER BY name")
    suspend fun getAllSync(): List<BikeType>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bikeType: BikeType)

    @Update
    suspend fun update(bikeType: BikeType)

    @Delete
    suspend fun delete(bikeType: BikeType)

    @Query("DELETE FROM bike_types")
    suspend fun deleteAll()
}