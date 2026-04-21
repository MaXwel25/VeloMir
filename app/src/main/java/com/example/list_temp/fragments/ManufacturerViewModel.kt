package com.example.list_temp.fragments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.list_temp.MyConsts.TAG
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.repository.AppRepository

class ManufacturerViewModel : ViewModel() {

    var manufacturerList: MutableLiveData<List<Manufacturer>> = MutableLiveData()
    private var _manufacturer: Manufacturer? = null
    val manufacturer get() = _manufacturer

    init {
        AppRepository.getInstance().listOfManufacturer.observeForever {
            manufacturerList.postValue(AppRepository.getInstance().bikeTypeManufacturers)
        }

        AppRepository.getInstance().manufacturer.observeForever {
            _manufacturer = it
            Log.d(TAG, "ManufacturerViewModel Текущий производитель $it")
        }

        AppRepository.getInstance().bikeType.observeForever {
            manufacturerList.postValue(AppRepository.getInstance().bikeTypeManufacturers)
        }
    }

    fun deleteManufacturer() {
        if (manufacturer != null)
            AppRepository.getInstance().deleteManufacturer(manufacturer!!)
    }

    fun appendManufacturer(name: String) {
        val manufacturer = Manufacturer()
        manufacturer.name = name
        manufacturer.bikeTypeID = bikeType?.id
        AppRepository.getInstance().updateManufacturer(manufacturer)
    }

    fun updateManufacturer(name: String) {
        if (_manufacturer != null) {
            _manufacturer!!.name = name
            AppRepository.getInstance().updateManufacturer(_manufacturer!!)
        }
    }

    fun setCurrentManufacturer(position: Int) {
        if ((manufacturerList.value?.size ?: 0) > position)
            manufacturerList.value?.let { AppRepository.getInstance().setCurrentManufacturer(it[position]) }
    }

    fun setCurrentManufacturer(manufacturer: Manufacturer) {
        AppRepository.getInstance().setCurrentManufacturer(manufacturer)
    }

    val getManufacturerListPosition
        get() = manufacturerList.value?.indexOfFirst { it.id == manufacturer?.id } ?: -1

    val bikeType
        get() = AppRepository.getInstance().bikeType.value
}