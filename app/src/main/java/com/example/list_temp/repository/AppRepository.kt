package com.example.list_temp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.list_temp.MyApplication
import com.example.list_temp.MyConsts.TAG
import com.example.list_temp.R
import com.example.list_temp.data.BikeType
import com.example.list_temp.data.ListOfBikeType
import com.example.list_temp.data.ListOfManufacturer
import com.example.list_temp.data.ListOfBikeModel
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.data.BikeModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.lang.IllegalStateException
import java.util.UUID

class AppRepository {

    companion object {
        private var INSTANCE: AppRepository? = null

        fun getInstance(): AppRepository {
            if (INSTANCE == null) {
                INSTANCE = AppRepository()
            }
            return INSTANCE ?: throw IllegalStateException("Репозиторий не инициализирован")
        }
    }

    // Типы велосипедов
    var listOfBikeType: MutableLiveData<ListOfBikeType?> = MutableLiveData()
    var bikeType: MutableLiveData<BikeType> = MutableLiveData()

    // Производители
    var listOfManufacturer: MutableLiveData<ListOfManufacturer?> = MutableLiveData()
    var manufacturer: MutableLiveData<Manufacturer> = MutableLiveData()

    // Модели велосипедов
    var listOfBikeModel: MutableLiveData<ListOfBikeModel?> = MutableLiveData()
    var bikeModel: MutableLiveData<BikeModel> = MutableLiveData()

    // === Работа с типами велосипедов ===
    fun addBikeType(bikeType: BikeType) {
        val listTmp = (listOfBikeType.value ?: ListOfBikeType()).apply {
            items.add(bikeType)
        }
        listOfBikeType.postValue(listTmp)
        setCurrentBikeType(bikeType)
    }

    fun getBikeTypePosition(bikeType: BikeType): Int =
        listOfBikeType.value?.items?.indexOfFirst { it.id == bikeType.id } ?: -1

    fun getBikeTypePosition() = getBikeTypePosition(bikeType.value ?: BikeType())

    fun setCurrentBikeType(position: Int) {
        if (listOfBikeType.value == null || position < 0 ||
            (listOfBikeType.value?.items?.size!! <= position)
        )
            return
        setCurrentBikeType(listOfBikeType.value?.items!![position])
    }

    fun setCurrentBikeType(_bikeType: BikeType) {
        bikeType.postValue(_bikeType)
    }

    fun updateBikeType(bikeType: BikeType) {
        val position = getBikeTypePosition(bikeType)
        if (position < 0) addBikeType(bikeType)
        else {
            val listTmp = listOfBikeType.value!!
            listTmp.items[position] = bikeType
            listOfBikeType.postValue(listTmp)
        }
    }

    fun deleteBikeType(bikeType: BikeType) {
        val listTmp = listOfBikeType.value!!
        if (listTmp.items.remove(bikeType)) {
            listOfBikeType.postValue(listTmp)
        }
        setCurrentBikeType(0)
    }

    // === Сохранение и загрузка данных ===
    fun saveData() {
        val context = MyApplication.context
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().apply {
            val gson = Gson()
            val lst = listOfBikeType.value?.items ?: listOf<BikeType>()
            val jsonString = gson.toJson(lst)
            Log.d(TAG, "Сохранение $jsonString")
            putString(context.getString(R.string.preference_key_faculty_list), jsonString)
            putString(
                context.getString(R.string.preference_key_group_list),
                gson.toJson(listOfManufacturer.value?.items ?: listOf<Manufacturer>())
            )
            putString(
                context.getString(R.string.preference_key_students_list),
                gson.toJson(listOfBikeModel.value?.items ?: listOf<BikeModel>())
            )
            apply()
        }
    }

    fun loadData() {
        val context = MyApplication.context
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.apply {
            val jsonString = getString(context.getString(R.string.preference_key_faculty_list), null)
            if (jsonString != null) {
                Log.d(TAG, "Чтение $jsonString")
                val listType = object : TypeToken<List<BikeType>>() {}.type
                val tempList = Gson().fromJson<List<BikeType>>(jsonString, listType)
                val temp = ListOfBikeType()
                temp.items = tempList.toMutableList()
                Log.d(TAG, "Загрузка ${temp.toString()}")
                listOfBikeType.postValue(temp)
            }
            val jsonStringG = getString(context.getString(R.string.preference_key_group_list), null)
            if (jsonStringG != null) {
                val listTypeG = object : TypeToken<List<Manufacturer>>() {}.type
                val tempListG = Gson().fromJson<List<Manufacturer>>(jsonStringG, listTypeG)
                val tempG = ListOfManufacturer()
                tempG.items = tempListG.toMutableList()
                listOfManufacturer.postValue(tempG)
            }
            val jsonStringS = getString(context.getString(R.string.preference_key_students_list), null)
            if (jsonStringS != null) {
                val listTypeS = object : TypeToken<List<BikeModel>>() {}.type
                val tempListS = Gson().fromJson<List<BikeModel>>(jsonStringS, listTypeS)
                val tempS = ListOfBikeModel()
                tempS.items = tempListS.toMutableList()
                listOfBikeModel.postValue(tempS)
            }
        }
    }

    // === Работа с производителями ===
    fun addManufacturer(manufacturer: Manufacturer) {
        val listTmp = (listOfManufacturer.value ?: ListOfManufacturer()).apply {
            items.add(manufacturer)
        }
        listOfManufacturer.postValue(listTmp)
        setCurrentManufacturer(manufacturer)
    }

    fun getManufacturerPosition(manufacturer: Manufacturer): Int =
        listOfManufacturer.value?.items?.indexOfFirst { it.id == manufacturer.id } ?: -1

    fun getManufacturerPosition() = getManufacturerPosition(manufacturer.value ?: Manufacturer())

    fun setCurrentManufacturer(position: Int) {
        if (listOfManufacturer.value == null || position < 0 ||
            (listOfManufacturer.value?.items?.size!! <= position)
        )
            return
        setCurrentManufacturer(listOfManufacturer.value?.items!![position])
    }

    fun setCurrentManufacturer(_manufacturer: Manufacturer) {
        manufacturer.postValue(_manufacturer)
    }

    fun updateManufacturer(manufacturer: Manufacturer) {
        val position = getManufacturerPosition(manufacturer)
        if (position < 0) addManufacturer(manufacturer)
        else {
            val listTmp = listOfManufacturer.value!!
            listTmp.items[position] = manufacturer
            listOfManufacturer.postValue(listTmp)
        }
    }

    fun deleteManufacturer(manufacturer: Manufacturer) {
        val listTmp = listOfManufacturer.value ?: ListOfManufacturer()
        if (listTmp.items.remove(manufacturer))
            listOfManufacturer.postValue(listTmp)
        setCurrentManufacturer(0)
    }

    val bikeTypeManufacturers
        get() = listOfManufacturer.value?.items?.filter {
            it.bikeTypeID == bikeType.value?.id
        }?.sortedBy { it.name } ?: listOf()

    fun getBikeTypeManufacturers(bikeTypeID: UUID) =
        listOfManufacturer.value?.items?.filter { it.bikeTypeID == bikeTypeID }?.sortedBy { it.name } ?: listOf()

    // === Работа с моделями велосипедов ===
    fun addBikeModel(bikeModel: BikeModel) {
        val listTmp = (listOfBikeModel.value ?: ListOfBikeModel()).apply {
            items.add(bikeModel)
        }
        listOfBikeModel.postValue(listTmp)
        setCurrentBikeModel(bikeModel)
    }

    fun getBikeModelPosition(bikeModel: BikeModel): Int =
        listOfBikeModel.value?.items?.indexOfFirst { it.id == bikeModel.id } ?: -1

    fun getBikeModelPosition() = getBikeModelPosition(bikeModel.value ?: BikeModel())

    fun setCurrentBikeModel(position: Int) {
        if (listOfBikeModel.value == null || position < 0 ||
            (listOfBikeModel.value?.items?.size!! <= position)
        )
            return
        setCurrentBikeModel(listOfBikeModel.value?.items!![position])
    }

    fun setCurrentBikeModel(_bikeModel: BikeModel) {
        bikeModel.postValue(_bikeModel)
    }

    fun updateBikeModel(bikeModel: BikeModel) {
        val position = getBikeModelPosition(bikeModel)
        if (position < 0) addBikeModel(bikeModel)
        else {
            val listTmp = listOfBikeModel.value!!
            listTmp.items[position] = bikeModel
            listOfBikeModel.postValue(listTmp)
        }
    }

    fun deleteBikeModel(bikeModel: BikeModel) {
        val listTmp = listOfBikeModel.value ?: ListOfBikeModel()
        if (listTmp.items.remove(bikeModel))
            listOfBikeModel.postValue(listTmp)
        setCurrentBikeModel(0)
    }

    val manufacturerBikeModels
        get() = listOfBikeModel.value?.items?.filter {
            it.manufacturerID == manufacturer.value?.id
        }?.sortedBy { it.name } ?: listOf()

    fun getManufacturerBikeModels(manufacturerID: UUID) =
        listOfBikeModel.value?.items?.filter { it.manufacturerID == manufacturerID }?.sortedBy { it.name } ?: listOf()
}