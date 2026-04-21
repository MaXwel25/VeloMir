package com.example.list_temp.database


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.list_temp.data.Manufacturer

@Dao
interface ManufacturerDao {
    @Query("SELECT * FROM manufacturers WHERE bikeTypeId = :typeId ORDER BY name")
    fun getByType(typeId: String): LiveData<List<Manufacturer>>

    @Query("SELECT * FROM manufacturers ORDER BY name")
    suspend fun getAllSync(): List<Manufacturer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(manufacturer: Manufacturer)

    @Update
    suspend fun update(manufacturer: Manufacturer)

    @Delete
    suspend fun delete(manufacturer: Manufacturer)

    @Query("DELETE FROM manufacturers")
    suspend fun deleteAll()
}