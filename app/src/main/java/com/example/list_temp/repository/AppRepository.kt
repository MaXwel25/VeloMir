package com.example.list_temp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.list_temp.MyApplication
import com.example.list_temp.MyConsts.TAG
import com.example.list_temp.R
import com.example.list_temp.data.Faculty
import com.example.list_temp.data.Group
import com.example.list_temp.data.ListOfFaculty
import com.example.list_temp.data.ListOfGroup
import com.example.list_temp.data.ListOfStudent
import com.example.list_temp.data.Student
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.lang.IllegalStateException
import java.util.UUID

class AppRepository {

    companion object{
        private var INSTANCE: AppRepository?=null

        fun getInstance(): AppRepository{
            if(INSTANCE ==null){
                INSTANCE = AppRepository()
            }
            return INSTANCE ?:
            throw IllegalStateException("Репозиторий не инициализирован")
        }
    }

    var listOfFaculty: MutableLiveData<ListOfFaculty?> = MutableLiveData()
    var faculty : MutableLiveData<Faculty> = MutableLiveData()

    var listOfGroup: MutableLiveData<ListOfGroup?> = MutableLiveData()
    var group : MutableLiveData<Group> = MutableLiveData()

    var listOfStudent: MutableLiveData<ListOfStudent?> = MutableLiveData()
    var student : MutableLiveData<Student> = MutableLiveData()

    fun addFaculty(faculty: Faculty){
        val listTmp = (listOfFaculty.value ?: ListOfFaculty()).apply {
            items.add(faculty)
        }
        listOfFaculty.postValue(listTmp)
        setCurrentFaculty(faculty)
    }

    fun getFacultyPosition(faculty: Faculty):Int = listOfFaculty.value?.items?.indexOfFirst {
        it.id==faculty.id} ?: -1

    fun getFacultyPosition()=getFacultyPosition(faculty.value?: Faculty())

    fun setCurrentFaculty(position: Int){
        if(listOfFaculty.value==null || position<0 ||
            (listOfFaculty.value?.items?.size!!<=position))
            return
        setCurrentFaculty(listOfFaculty.value?.items!![position])
    }
    fun setCurrentFaculty(_faculty:Faculty){
        faculty.postValue(_faculty)
    }
    fun updateFaculty(faculty: Faculty){
        val position = getFacultyPosition(faculty)
        if (position < 0) addFaculty(faculty)
        else {
            val listTmp = listOfFaculty.value!!
            listTmp.items[position]=faculty
            listOfFaculty.postValue(listTmp)
        }
    }

    fun deleteFaculty(faculty: Faculty){
        val listTmp = listOfFaculty.value!!
        if(listTmp.items.remove(faculty)){
            listOfFaculty.postValue(listTmp)
        }
        setCurrentFaculty(0)
    }

    fun saveData(){
        val context= MyApplication.context
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().apply{
            val gson = Gson()
            val lst=listOfFaculty.value?.items ?: listOf<Faculty>()
            val jsonString =gson.toJson(lst)
            Log.d(TAG,"Сохранение $jsonString")
            putString(context.getString(R.string.preference_key_faculty_list), jsonString)
            putString(context.getString(R.string.preference_key_group_list), gson.toJson(listOfGroup.value?.items ?: listOf<Group>()))
            putString(context.getString(R.string.preference_key_students_list), gson.toJson(listOfStudent.value?.items ?: listOf<Student>()))
            apply()
        }
    }
    fun loadData(){
        val context= MyApplication.context
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.apply{
            val jsonString =getString(context.getString(R.string.preference_key_faculty_list),null)
            if (jsonString!=null) {
                Log.d(TAG, "Чтение $jsonString")
                val listType = object : TypeToken<List<Faculty>>() {}.type
                val tempList = Gson().fromJson<List<Faculty>>(jsonString, listType)
                val temp = ListOfFaculty()
                temp.items = tempList.toMutableList()
                Log.d(TAG, "Загрузка ${temp.toString()}")
                listOfFaculty.postValue(temp)

            }
            val jsonStringG =getString(context.getString(R.string.preference_key_group_list),null)
            if (jsonStringG!=null) {
                val listTypeG = object : TypeToken<List<Group>>() {}.type
                val tempListG = Gson().fromJson<List<Group>>(jsonStringG, listTypeG)
                val tempG = ListOfGroup()
                tempG.items = tempListG.toMutableList()
                listOfGroup.postValue(tempG)

            }
            val jsonStringS =getString(context.getString(R.string.preference_key_students_list),null)
            if (jsonStringS!=null) {
                val listTypeS = object : TypeToken<List<Student>>() {}.type
                val tempListS = Gson().fromJson<List<Student>>(jsonStringS, listTypeS)
                val tempS = ListOfStudent()
                tempS.items = tempListS.toMutableList()
                listOfStudent.postValue(tempS)

            }
        }
    }

    fun addGroup(group: Group){
        val listTmp = (listOfGroup.value ?: ListOfGroup()).apply {
            items.add(group)
        }
        listOfGroup.postValue(listTmp)
        setCurrentGroup(group)
    }

    fun getGroupPosition(group: Group):Int = listOfGroup.value?.items?.indexOfFirst {
        it.id==group.id} ?:-1
    
    fun getGroupPosition()=getGroupPosition(group.value?: Group())

    fun setCurrentGroup(position: Int){
        if(listOfGroup.value==null || position<0 ||
            (listOfGroup.value?.items?.size!!<=position))
            return
        setCurrentGroup(listOfGroup.value?.items!![position])
    }

    fun setCurrentGroup (_group:Group){
        group.postValue(_group)
    }

    fun updateGroup(group: Group){
        val position = getGroupPosition(group)
        if (position < 0) addGroup(group)
        else {
            val listTmp = listOfGroup.value!!
            listTmp.items[position]=group
            listOfGroup.postValue(listTmp)
        }
    }

    fun deleteGroup(group: Group){
        val listTmp = listOfGroup.value ?: ListOfGroup()
        if(listTmp.items.remove(group))
            listOfGroup.postValue(listTmp)
        setCurrentGroup(0)
    }

    val facultyGroups
      get()= listOfGroup.value?.items?.filter{
          it.facultyID == (faculty.value?.id ?: 0)}?.sortedBy { it.name } ?: listOf()

    fun getFacultyGroups(facultyID: UUID) =
        (listOfGroup.value?.items?.filter{ it.facultyID == facultyID }?.sortedBy { it.name } ?: listOf())


    fun addStudent(student: Student){
        val listTmp = (listOfStudent.value ?: ListOfStudent()).apply {
            items.add(student)
        }
        listOfStudent.postValue(listTmp)
        setCurrentStudent(student)
    }

    fun getStudentPosition(student: Student):Int = listOfStudent.value?.items?.indexOfFirst {
        it.id==student.id} ?:-1

    fun getStudentPosition()=getStudentPosition(student.value?: Student())

    fun setCurrentStudent(position: Int){
        if(listOfStudent.value==null || position<0 ||
            (listOfStudent.value?.items?.size!!<=position))
            return
        setCurrentStudent(listOfStudent.value?.items!![position])
    }

    fun setCurrentStudent (_student:Student){
        student.postValue(_student)
    }

    fun updateStudent(student: Student){
        val position = getStudentPosition(student)
        if (position < 0) addStudent(student)
        else {
            val listTmp = listOfStudent.value!!
            listTmp.items[position]=student
            listOfStudent.postValue(listTmp)
        }
    }

    fun deleteStudent(student: Student){
        val listTmp = listOfStudent.value ?: ListOfStudent()
        if(listTmp.items.remove(student)) listOfStudent.postValue(listTmp)
        setCurrentStudent(0)
    }

    val groupStudents
       get()= listOfStudent.value?.items?.filter {
           it.groupID== (group.value?.id ?: 0) }?.sortedBy { it.shortName } ?: listOf()

    fun getGroupStudents(groupID: UUID) =
        listOfStudent.value?.items?.filter { it.groupID == groupID }?.sortedBy {it.shortName } ?: listOf()
}