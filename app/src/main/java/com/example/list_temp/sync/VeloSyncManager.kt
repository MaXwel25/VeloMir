package com.example.list_temp.sync

import android.content.Context
import com.example.list_temp.data.BikeType
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.data.BikeModel
import com.example.list_temp.database.AppDatabase
import com.example.list_temp.network.NetworkModule
import com.example.list_temp.network.SyncData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VeloSyncManager(private val context: Context) {

    private val api = NetworkModule.apiService
    private val db = AppDatabase.getDatabase(context)

    // Отправка всех локальных данных на сервер (полная замена)
    suspend fun pushAllToServer(): SyncResult = withContext(Dispatchers.IO) {
        try {
            val types = db.bikeTypeDao().getAllSync()
            val manufacturers = db.manufacturerDao().getAllSync()
            val models = db.bikeModelDao().getAllSync()

            val response = api.syncAll(SyncData(types, manufacturers, models))
            if (response.success) {
                SyncResult.Success("Данные отправлены на сервер")
            } else {
                SyncResult.Error("Ошибка сервера: ${response.message}")
            }
        } catch (e: Exception) {
            SyncResult.Error("Ошибка сети: ${e.message}")
        }
    }

    // получение всех данных с сервера и замена локальной БД
    suspend fun pullAllFromServer(): SyncResult = withContext(Dispatchers.IO) {
        try {
            // получаем все три списка
            val types = api.getBikeTypes()
            val manufacturers = api.getManufacturers()
            val models = api.getModels()

            // очищаем локальную БД и вставляем новые данные
            db.bikeTypeDao().deleteAll()
            db.manufacturerDao().deleteAll()
            db.bikeModelDao().deleteAll()

            types.forEach { db.bikeTypeDao().insert(it) }
            manufacturers.forEach { db.manufacturerDao().insert(it) }
            models.forEach { db.bikeModelDao().insert(it) }

            SyncResult.Success("Загружено: ${types.size} типов, ${manufacturers.size} производителей, ${models.size} моделей")
        } catch (e: Exception) {
            SyncResult.Error("Ошибка загрузки: ${e.message}")
        }
    }
}

sealed class SyncResult {
    data class Success(val message: String) : SyncResult()
    data class Error(val message: String) : SyncResult()
}