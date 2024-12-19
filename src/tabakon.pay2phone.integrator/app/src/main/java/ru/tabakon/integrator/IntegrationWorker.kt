package ru.tabakon.integrator

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ru.tabakon.integrator.activities.main.MainActivity
import ru.tabakon.integrator.p2pimp.Integrator
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date


class IntegrationWorker : Service() {


    companion object {

        private val CHANNEL_ID = "TabakonIntegratorService"

        /*lateinit var broadCastReceiver: BroadcastReceiver;*/

        var integrator: Integrator = Integrator();

        val NOTIF_ID = 1;
        var isStopped = true;

        fun startService(context: Context, hostName: String) {
            log("startService")
            isStopped = false;
            val startIntent = Intent(context, IntegrationWorker::class.java)
            startIntent.putExtra("inputExtra", hostName)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            log("stopService")
            isStopped = true;

            integrator.stop();

            val stopIntent = Intent(context, IntegrationWorker::class.java)
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")

        //do heavy work on a background thread
        val hostName = intent?.getStringExtra("inputExtra")
        createNotificationChannel()

        val notification = getNotification(hostName ?: "???");

        startForeground(NOTIF_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)

          Thread {
            val uri = URI("http://$hostName/");
            val context = getContext();
            log(hostName!!);
            integrator.start(context, uri);
        }.start()

        Thread {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "IntegrationWorker::SimpleDateFormat"
            )
            wakeLock.acquire(10*60*1000L /*10 minutes*/)

            while (!isStopped) {
                //rg()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                notify("qwe $currentDate")

                //val uri = URI("ws://$hostName/");
                //val context = getContext();
                //integrator.start(context, uri);

                //val q = integrator != null;
                //sendGet();
                updateNotification("qwe $currentDate");
                Thread.sleep(5000);
            }
            wakeLock.release();
        }.start();

        return START_STICKY
    }

    private fun  getContext():Context{
        var cont = MainActivity.context;
        return cont;
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /*private fun createNotificationChannel() {
        log("createNotificationChannel")

        val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }*/

    private fun notify(msg: String){
        //val manager = getSystemService(NotificationManager::class.java);
        log("notify $msg")
        //manager.notify(NOTIF_ID, getNotification(msg));
    }

    private fun getNotification(msg: String) : Notification{
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_MUTABLE
        )

        val notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }
}



