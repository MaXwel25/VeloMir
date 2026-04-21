package com.example.list_temp.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.list_temp.data.BikeType
import com.example.list_temp.data.ListOfBikeType
import com.example.list_temp.repository.AppRepository

class BikeTypeViewModel : ViewModel() {
    var bikeTypeList: MutableLiveData<ListOfBikeType?> = MutableLiveData()
    private var _bikeType : BikeType? = null
    val bikeType
        get()=_bikeType

    private val bikeTypeListObserver = Observer<ListOfBikeType?>{
            list ->
        bikeTypeList.postValue(list)
    }

    init {
        AppRepository.getInstance().listOfBikeType.observeForever(bikeTypeListObserver)
        AppRepository.getInstance().bikeType.observeForever{
            _bikeType=it
        }
    }

    fun deleteBikeType(){
        if (bikeType!=null)
            AppRepository.getInstance().deleteBikeType(bikeType!!)
    }

    fun appendBikeType(name : String){
        val bikeType= BikeType()
        bikeType.name=name
        AppRepository.getInstance().updateBikeType(bikeType)
    }


    fun updateBikeType(name : String){
        if (_bikeType!=null){
            _bikeType!!.name=name
            AppRepository.getInstance().updateBikeType(_bikeType!!)
        }
    }

    fun setCurrentBikeType(bikeType: BikeType){
        AppRepository.getInstance().setCurrentBikeType(bikeType)
    }
}