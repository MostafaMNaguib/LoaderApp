package com.mostafa.udacity.myapplication

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mostafa.udacity.myapplication.utils.notification.sendNotification
import com.mostafa.udacity.myapplication.utils.view.ButtonState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var downloadedFile = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (repoUrl.isEmpty()) {
                Toast.makeText(applicationContext, "Select a file to download", Toast.LENGTH_SHORT)
                    .show()

            }else {
                download()
            }
        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            intent.apply {
                if (this?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE && id == downloadID){
                    val downloadQuery = DownloadManager.Query().setFilterById(downloadID)
                    val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val cursor : Cursor = downloadManager.query(downloadQuery)
                    if (cursor.moveToFirst()){
                        when ( cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_FAILED -> {
                                notificationManager.sendNotification("FAILED",downloadedFile, context!!
                                )
                            }
                            DownloadManager.STATUS_RUNNING -> {
                                notificationManager.sendNotification("RUNNING",downloadedFile,context!!)
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                notificationManager.sendNotification("SUCCESS",downloadedFile,context!!)
                            }
                        }
                        custom_button.updateBtnState(ButtonState.Completed)
                        custom_button.isEnabled = true
                    }
                }
            }
        }
    }

    private fun download() {

        custom_button.updateBtnState(ButtonState.Loading)
        custom_button.isEnabled = false

        notificationManager = ContextCompat.getSystemService(applicationContext,NotificationManager::class.java) as NotificationManager
        createChannel(getString(R.string.file_notification_channel_id),getString(R.string.file_notification_channel_name))

        val request =
            DownloadManager.Request(Uri.parse(repoUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    fun onRadioBtnChecked(view : View) {
        if (view is RadioButton) {
            val isChecked = view.isChecked

            when (view.id) {
                R.id.glide_rb -> {
                    if (isChecked) {
                        custom_button.isEnabled = true
                        repoUrl = "https://github.com/bumptech/glide/archive/master.git"
                        downloadedFile = getString(R.string.glide_rb_txt)
                    }
                }
                R.id.load_app_rb -> {
                    if (isChecked) {
                        custom_button.isEnabled = true
                        repoUrl =
                            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                        downloadedFile = getString(R.string.load_app_rb_txt)
                    }
                }
                R.id.retrofit_rb -> {
                    if (isChecked) {
                        custom_button.isEnabled = true
                        repoUrl = "https://github.com/square/retrofit/archive/master.zip"
                        downloadedFile = getString(R.string.retrofit_rb_txt)
                    }
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private var repoUrl = ""


        // https://github.com/bumptech/glide >> https://github.com/bumptech/glide/archive/master.git
        // https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter
        // https://github.com/square/retrofit
    }


    override fun onDestroy() {
        super.onDestroy()
        repoUrl = ""
    }


}