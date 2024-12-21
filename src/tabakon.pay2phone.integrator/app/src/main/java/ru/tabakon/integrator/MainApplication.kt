package ru.tabakon.integrator

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import ru.tabakon.integrator.utilits.SettingsStorage


class MainApplication: Application() {

    init {
        instance = this
        settingsStorage = SettingsStorage(this);
    }
    companion object {
        private lateinit var instance: MainApplication

        @SuppressLint("StaticFieldLeak")
        private lateinit var settingsStorage : SettingsStorage

        fun applicationContext() : Context {
            return instance.applicationContext
        }

        fun settingsStorage() : SettingsStorage {
            return settingsStorage;
        }
    }

    override fun onCreate(){
        super.onCreate();

        requestIgnoreBatteryOptimisation(applicationContext())


        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext())
        val areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled()
        log(applicationContext(), "areNotificationsEnabled=$areNotificationsEnabled")

    }

    @SuppressLint("BatteryLife")
    fun requestIgnoreBatteryOptimisation(context: Context) {
        val intent = Intent()
        val packageName = context.packageName
        val pm = context.getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:$packageName"))
            context.startActivity(intent)
        }
    }
}