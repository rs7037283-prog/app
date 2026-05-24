package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Draft
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    
    var isInitialized = false
        private set

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            // Check if default initialization was completed (e.g., if a developer placed a google-services.json)
            FirebaseApp.getInstance()
            isInitialized = true
            Log.d(TAG, "Firebase initialized via default XML provider (google-services.json).")
        } catch (e: Exception) {
            // Attempt programmatic configuration using the secrets specified in AI Studio / .env
            try {
                val apiKey = BuildConfig.FIREBASE_API_KEY
                val projectId = BuildConfig.FIREBASE_PROJECT_ID
                val appId = BuildConfig.FIREBASE_APPLICATION_ID

                if (apiKey.isNotBlank() && projectId.isNotBlank() && appId.isNotBlank() &&
                    apiKey != "MY_FIREBASE_API_KEY" && projectId != "MY_FIREBASE_PROJECT_ID" &&
                    appId != "MY_FIREBASE_APPLICATION_ID") {
                    
                    val options = FirebaseOptions.Builder()
                        .setApiKey(apiKey)
                        .setProjectId(projectId)
                        .setApplicationId(appId)
                        .build()
                    
                    FirebaseApp.initializeApp(context.applicationContext, options)
                    isInitialized = true
                    Log.d(TAG, "Firebase programmatically initialized successfully with dynamic options.")
                } else {
                    Log.w(TAG, "Firebase credentials omitted or set to placeholders in Secrets. App will run in Local Sandbox mode.")
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Dynamic Firebase init failed. Check your environment variables.", ex)
            }
        }
    }

    val auth: FirebaseAuth?
        get() = if (isInitialized) FirebaseAuth.getInstance() else null

    val firestore: FirebaseFirestore?
        get() = if (isInitialized) FirebaseFirestore.getInstance() else null
}
