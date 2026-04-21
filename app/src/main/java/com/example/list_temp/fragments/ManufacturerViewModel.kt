package com.example.list_temp.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.repository.VeloRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.switchMap

class ManufacturerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VeloRepository(application)

    private val _currentBikeTypeId = MutableLiveData<String?>()
    val currentBikeTypeId: LiveData<String?> = _currentBikeTypeId

    private val _currentManufacturer = MutableLiveData<Manufacturer?>()
    val currentManufacturer: LiveData<Manufacturer?> = _currentManufacturer

    // Список производителей для текущего типа (будет обновляться при изменении currentBikeTypeId)
    val manufacturers: LiveData<List<Manufacturer>> = _currentBikeTypeId.switchMap { typeId ->
        typeId?.let { repository.getManufacturersByType(it) } ?: MutableLiveData(emptyList())
    }

    fun setCurrentBikeTypeId(typeId: String) {
        _currentBikeTypeId.value = typeId
    }

    fun setCurrentManufacturer(manufacturer: Manufacturer) {
        _currentManufacturer.value = manufacturer
    }

    fun addManufacturer(name: String) = viewModelScope.launch {
        val typeId = _currentBikeTypeId.value ?: return@launch
        val manufacturer = Manufacturer(name = name, bikeTypeId = typeId)
        repository.insertManufacturer(manufacturer)
    }

    fun updateManufacturer(manufacturer: Manufacturer, newName: String) = viewModelScope.launch {
        manufacturer.name = newName
        repository.updateManufacturer(manufacturer)
    }

    fun deleteManufacturer(manufacturer: Manufacturer) = viewModelScope.launch {
        repository.deleteManufacturer(manufacturer)
        if (_currentManufacturer.value?.id == manufacturer.id) {
            _currentManufacturer.value = null
        }
    }
}