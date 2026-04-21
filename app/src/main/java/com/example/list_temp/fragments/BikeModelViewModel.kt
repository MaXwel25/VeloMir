package com.example.list_temp.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.list_temp.data.BikeModel
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.repository.AppRepository

class BikeModelViewModel : ViewModel() {
    var bikeModelList: MutableLiveData<List<BikeModel>> = MutableLiveData()

    private var _bikeModel: BikeModel? = null
    val bikeModel get() = _bikeModel

    lateinit var manufacturer: Manufacturer

    fun initManufacturer(manufacturer: Manufacturer) {
        this.manufacturer = manufacturer
        AppRepository.getInstance().listOfBikeModel.observeForever {
            bikeModelList.postValue(AppRepository.getInstance().getManufacturerBikeModels(manufacturer.id))
        }
        AppRepository.getInstance().bikeModel.observeForever {
            _bikeModel = it
        }
    }

    fun deleteBikeModel() {
        if (bikeModel != null)
            AppRepository.getInstance().deleteBikeModel(bikeModel!!)
    }

    fun appendBikeModel(name: String, phone: String) {
        val model = BikeModel()
        model.name = name
        model.phone = phone
        model.manufacturerID = manufacturer.id
        AppRepository.getInstance().updateBikeModel(model)
    }

    fun updateBikeModel(name: String, phone: String) {
        if (_bikeModel != null) {
            _bikeModel!!.name = name
            _bikeModel!!.phone = phone
            AppRepository.getInstance().updateBikeModel(_bikeModel!!)
        }
    }

    fun setCurrentBikeModel(model: BikeModel) {
        AppRepository.getInstance().setCurrentBikeModel(model)
    }
}