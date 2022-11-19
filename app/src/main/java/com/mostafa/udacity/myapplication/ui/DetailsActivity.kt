package com.mostafa.udacity.myapplication.ui

import android.app.NotificationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.mostafa.udacity.myapplication.MainActivity
import com.mostafa.udacity.myapplication.R
import com.mostafa.udacity.myapplication.utils.notification.cancelNotifications
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailsActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        setSupportActionBar(toolbar)
        file_name.text= intent.getStringExtra("fileName").toString()
        file_status.text = intent.getStringExtra("status").toString()

        notificationManager = ContextCompat.getSystemService(applicationContext,NotificationManager::class.java) as NotificationManager

        notificationManager.cancelNotifications()

        fab.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

    }
}