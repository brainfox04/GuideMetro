package com.example.guidemetro

import android.app.Application
import com.google.firebase.FirebaseApp

class GuideMetroApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
