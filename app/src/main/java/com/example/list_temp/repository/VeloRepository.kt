package com.example.list_temp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.list_temp.data.BikeType
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.data.BikeModel
import com.example.list_temp.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VeloRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)

    // типы велосипедов
    fun getAllBikeTypes(): LiveData<List<BikeType>> = db.bikeTypeDao().getAll()

    suspend fun insertBikeType(type: BikeType) = withContext(Dispatchers.IO) {
        db.bikeTypeDao().insert(type)
    }

    suspend fun updateBikeType(type: BikeType) = withContext(Dispatchers.IO) {
        db.bikeTypeDao().update(type)
    }

    suspend fun deleteBikeType(type: BikeType) = withContext(Dispatchers.IO) {
        db.bikeTypeDao().delete(type)
    }

    // производители
    fun getManufacturersByType(typeId: String): LiveData<List<Manufacturer>> =
        db.manufacturerDao().getByType(typeId)

    suspend fun insertManufacturer(man: Manufacturer) = withContext(Dispatchers.IO) {
        db.manufacturerDao().insert(man)
    }

    suspend fun updateManufacturer(man: Manufacturer) = withContext(Dispatchers.IO) {
        db.manufacturerDao().update(man)
    }

    suspend fun deleteManufacturer(man: Manufacturer) = withContext(Dispatchers.IO) {
        db.manufacturerDao().delete(man)
    }

    // модели
    fun getModelsByManufacturer(manId: String): LiveData<List<BikeModel>> =
        db.bikeModelDao().getByManufacturer(manId)

    suspend fun insertModel(model: BikeModel) = withContext(Dispatchers.IO) {
        db.bikeModelDao().insert(model)
    }

    suspend fun updateModel(model: BikeModel) = withContext(Dispatchers.IO) {
        db.bikeModelDao().update(model)
    }

    suspend fun deleteModel(model: BikeModel) = withContext(Dispatchers.IO) {
        db.bikeModelDao().delete(model)
    }

    // для синхронизации — получение списков
    suspend fun getAllTypesSync(): List<BikeType> = db.bikeTypeDao().getAllSync()
    suspend fun getAllManufacturersSync(): List<Manufacturer> = db.manufacturerDao().getAllSync()
    suspend fun getAllModelsSync(): List<BikeModel> = db.bikeModelDao().getAllSync()

    companion object {
        @Volatile
        private var INSTANCE: VeloRepository? = null

        fun getInstance(context: Context): VeloRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VeloRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

}