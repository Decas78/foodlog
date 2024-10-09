package com.example.foodlog

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class FoodLogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        Log.d("FoodLogApplication", "Firebase initialized")
    }
}