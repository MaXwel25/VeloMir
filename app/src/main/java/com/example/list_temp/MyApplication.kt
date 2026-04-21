package com.example.list_temp

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Room инициализируется автоматически при первом обращении к базе.
        // Специальная загрузка не требуется.
    }

    companion object {
        private lateinit var instance: MyApplication

        val context: MyApplication
            get() = instance
    }

    init {
        instance = this
    }
}