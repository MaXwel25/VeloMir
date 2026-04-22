package com.example.list_temp.network


import com.example.list_temp.data.BikeType
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.data.BikeModel
import retrofit2.http.*

interface VeloApiService {

    // Типы
    @GET("bike-types")
    suspend fun getBikeTypes(): List<BikeType>

    @POST("bike-types")
    suspend fun addBikeType(@Body type: BikeType): BikeType

    @PUT("bike-types/{id}")
    suspend fun updateBikeType(@Path("id") id: String, @Body type: BikeType): BikeType

    @DELETE("bike-types/{id}")
    suspend fun deleteBikeType(@Path("id") id: String)

    // Производители
    @GET("manufacturers")
    suspend fun getManufacturers(): List<Manufacturer>

    @POST("manufacturers")
    suspend fun addManufacturer(@Body manufacturer: Manufacturer): Manufacturer

    @PUT("manufacturers/{id}")
    suspend fun updateManufacturer(@Path("id") id: String, @Body manufacturer: Manufacturer): Manufacturer

    @DELETE("manufacturers/{id}")
    suspend fun deleteManufacturer(@Path("id") id: String)

    // Модели
    @GET("models")
    suspend fun getModels(): List<BikeModel>

    @POST("models")
    suspend fun addModel(@Body model: BikeModel): BikeModel

    @PUT("models/{id}")
    suspend fun updateModel(@Path("id") id: String, @Body model: BikeModel): BikeModel

    @DELETE("models/{id}")
    suspend fun deleteModel(@Path("id") id: String)

    // полная синхронизация (отправка всех данных на сервер)
    @POST("sync/all")
    suspend fun syncAll(@Body data: SyncData): SyncResponse
}

data class SyncData(
    val bikeTypes: List<BikeType>,
    val manufacturers: List<Manufacturer>,
    val bikeModels: List<BikeModel>
)

data class SyncResponse(
    val success: Boolean,
    val message: String
)