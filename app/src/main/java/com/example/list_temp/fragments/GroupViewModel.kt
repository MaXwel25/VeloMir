package com.example.list_temp.fragments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.list_temp.MyConsts.TAG
import com.example.list_temp.data.Faculty
import com.example.list_temp.data.Group
import com.example.list_temp.data.ListOfFaculty
import com.example.list_temp.data.ListOfGroup
import com.example.list_temp.repository.AppRepository
import java.text.FieldPosition

class GroupViewModel : ViewModel() {

    var groupList: MutableLiveData<List<Group>> = MutableLiveData()
    private var _group : Group? = null
    val group
        get()=_group

    init {
        AppRepository.getInstance().listOfGroup.observeForever {
            groupList.postValue(AppRepository.getInstance().facultyGroups)
        }

        AppRepository.getInstance().group.observeForever{
            _group=it
            Log.d(TAG, "GroupViewModel Текущая группа $it")
        }

        AppRepository.getInstance().faculty.observeForever{
            groupList.postValue(AppRepository.getInstance().facultyGroups)
        }
    }
    fun deleteGroup(){
        if (group!=null)
            AppRepository.getInstance().deleteGroup(group!!)
    }
    fun appendGroup(groupName : String){
        val group=Group()
        group.name=groupName
        group.facultyID=faculty?.id
        AppRepository.getInstance().updateGroup(group)
    }
    fun updateGroup(groupName: String){
        if (_group!=null){
            _group!!.name=groupName
            AppRepository.getInstance().updateGroup(_group!!)
        }
    }
    fun setCurrentGroup(position: Int){
        if ((groupList.value?.size ?: 0) > position)
            groupList.value?.let { AppRepository.getInstance().setCurrentGroup(it.get(position))}
    }
    fun setCurrentGroup(group : Group){
        AppRepository.getInstance().setCurrentGroup(group)
    }
    val getGroupListPosition
        get()= groupList.value?.indexOfFirst {it.id==group?.id} ?:-1
    val faculty
        get()=AppRepository.getInstance().faculty.value
}