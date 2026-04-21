package com.example.list_temp.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.list_temp.data.BikeModel
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.repository.VeloRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.switchMap

class BikeModelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VeloRepository(application)

    private val _currentManufacturer = MutableLiveData<Manufacturer?>()
    val currentManufacturer: LiveData<Manufacturer?> = _currentManufacturer

    private val _currentBikeModel = MutableLiveData<BikeModel?>()
    val currentBikeModel: LiveData<BikeModel?> = _currentBikeModel

    val models: LiveData<List<BikeModel>> = _currentManufacturer.switchMap { man ->
        man?.let { repository.getModelsByManufacturer(it.id) } ?: MutableLiveData(emptyList())
    }

    fun setCurrentManufacturer(manufacturer: Manufacturer) {
        _currentManufacturer.value = manufacturer
    }

    fun setCurrentBikeModel(model: BikeModel) {
        _currentBikeModel.value = model
    }

    fun addBikeModel(name: String, phone: String) = viewModelScope.launch {
        val man = _currentManufacturer.value ?: return@launch
        val model = BikeModel(name = name, phone = phone, manufacturerId = man.id)
        repository.insertModel(model)
    }

    fun updateBikeModel(model: BikeModel, newName: String, newPhone: String) = viewModelScope.launch {
        model.name = newName
        model.phone = newPhone
        repository.updateModel(model)
    }

    fun deleteBikeModel(model: BikeModel) = viewModelScope.launch {
        repository.deleteModel(model)
        if (_currentBikeModel.value?.id == model.id) {
            _currentBikeModel.value = null
        }
    }
}