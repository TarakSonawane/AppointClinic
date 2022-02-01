package com.example.projectcllinic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ActivityAbout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar?.setTitle("About")
    }
}