package com.example.list_temp.interfaces

import com.example.list_temp.NamesOfFragment
import com.example.list_temp.data.Student

interface MainActivityCallbacks {
    fun newTitle(_title : String)
    fun showFragment(fragmentType: NamesOfFragment, student: Student? = null)
}
