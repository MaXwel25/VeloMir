package com.example.list_temp.interfaces

import com.example.list_temp.NamesOfFragment
import com.example.list_temp.data.BikeModel

interface MainActivityCallbacks {
    fun newTitle(_title: String)
    fun showFragment(fragmentType: NamesOfFragment, bikeModel: BikeModel? = null)
}