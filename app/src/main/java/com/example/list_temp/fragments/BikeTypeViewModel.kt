package com.example.list_temp.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.list_temp.data.BikeType
import com.example.list_temp.repository.VeloRepository
import kotlinx.coroutines.launch

class BikeTypeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VeloRepository(application)
    val allBikeTypes: LiveData<List<BikeType>> = repository.getAllBikeTypes()

    private val _currentBikeType = MutableLiveData<BikeType?>()
    val currentBikeType: LiveData<BikeType?> = _currentBikeType

    fun setCurrentBikeType(type: BikeType) {
        _currentBikeType.value = type
    }

    fun addBikeType(name: String) = viewModelScope.launch {
        val type = BikeType(name = name)
        repository.insertBikeType(type)
    }

    fun updateBikeType(type: BikeType, newName: String) = viewModelScope.launch {
        type.name = newName
        repository.updateBikeType(type)
    }

    fun deleteBikeType(type: BikeType) = viewModelScope.launch {
        repository.deleteBikeType(type)
        if (_currentBikeType.value?.id == type.id) {
            _currentBikeType.value = null
        }
    }
}