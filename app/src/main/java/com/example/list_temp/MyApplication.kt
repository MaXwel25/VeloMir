package com.example.list_temp

import android.app.Application
import android.content.Context
import com.example.list_temp.repository.AppRepository

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppRepository.getInstance().loadData()
    }

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        val context
            get()= applicationContext()

        private fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}