package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.data.local.LunaDatabase
import com.example.service.LunaVoiceForegroundService

class LunaApp : Application() {

    lateinit var database: LunaDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = LunaDatabase.getDatabase(this)
        createGlobalNotificationChannels()
    }

    private fun createGlobalNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                LunaVoiceForegroundService.CHANNEL_ID,
                "LUNA AI Voice Assistant",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "LUNA असिस्टेंट बैकग्राउंड लिसनिंग नोटिफिकेशन"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
