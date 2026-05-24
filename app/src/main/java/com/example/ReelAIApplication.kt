package com.example

import android.app.Application
import com.example.data.firebase.FirebaseManager
import com.example.data.local.AppDatabase
import com.example.data.repository.DraftRepository

class ReelAIApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: DraftRepository by lazy { DraftRepository(database.draftDao()) }

    override fun onCreate() {
        super.onCreate()
        FirebaseManager.initialize(this)
    }
}
