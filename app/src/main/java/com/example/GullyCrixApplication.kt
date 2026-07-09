package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.AppPreferences
import com.example.data.Repository

class GullyCrixApplication : Application() {
    lateinit var database: AppDatabase
    lateinit var repository: Repository
    lateinit var preferences: AppPreferences

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        repository = Repository(database)
        preferences = AppPreferences(this)
    }
}
