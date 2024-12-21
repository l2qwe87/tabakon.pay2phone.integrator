package ru.tabakon.integrator.host

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ru.tabakon.integrator.R
import ru.tabakon.integrator.activities.MainActivity
import ru.tabakon.integrator.log
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date

class MainService : Service()  {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {

        private val CHANNEL_ID = "TabakonIntegratorService"
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        @SuppressLint("StaticFieldLeak")
        private var mainWorker : MainWorker? = null




        val NOTIF_ID = 1;
        var isStopped = true;

        fun startService(context: Context, hostName: String) {
            isStopped = false;
            this.context = context;
            val startIntent = Intent(context, MainService::class.java)
            startIntent.putExtra("inputExtra", hostName)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            isStopped = true
            mainWorker?.stop()
            mainWorker = null
            val stopIntent = Intent(context, MainService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onDestroy() {
        log("onDestroy")
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent){
        log("onTaskRemoved")
        super.onTaskRemoved(rootIntent);
    }


    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    @SuppressLint("SimpleDateFormat")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")

        //do heavy work on a background thread
        val hostName = intent?.getStringExtra("inputExtra")
        createNotificationChannel()

        val notification = getNotification(hostName ?: "???");

        startForeground(NOTIF_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)

        Thread {
            val uri = URI("http://$hostName/")
            log(hostName!!);
            mainWorker = MainWorker(context, uri)
            mainWorker?.start()
        }.start()

        Thread {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "IntegrationWorker::SimpleDateFormat"
            )
            wakeLock.acquire(10*60*1000L /*10 minutes*/)

            while (!isStopped) {
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                notify("Tabakon keep a live $currentDate")
                updateNotification("Tabakon keep a live $currentDate");
                Thread.sleep(5000);
            }
            wakeLock.release();
        }.start();

        return START_STICKY
    }


    private fun notify(msg: String){
        //val manager = getSystemService(NotificationManager::class.java);
        log("notify $msg")
        //manager.notify(NOTIF_ID, getNotification(msg));
    }

    private fun getNotification(msg: String) : Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_MUTABLE
        )

        val notification = NotificationCompat
            .Builder(this, MainService.CHANNEL_ID)
            .setContentTitle("Табакон интегратор")
            .setContentText(msg)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        return notification;
    }

    private fun updateNotification(msg: String) {
        val text = msg
        val notification: Notification = getNotification(text)
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(NOTIF_ID, notification)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Tabakon foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)
    }
}