package ru.tabakon.integrator

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ru.tabakon.integrator.p2pimp.Integrator
import java.net.URI
import java.util.Date


class IntegrationWorker : Service() {


    companion object {

        private val CHANNEL_ID = "TabakonIntegratorService"

        private val integrator: Integrator = Integrator();

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
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")

        //do heavy work on a background thread
        val hostName = intent?.getStringExtra("inputExtra")
        createNotificationChannel()

        val notification = getNotification(hostName ?: "???");

        startForeground(NOTIF_ID, notification)

        //var payToPhoneClient: IPayToPhoneClient = PayToPhoneClient(getContext())
        //stopSelf();
        Thread {
            val uri = URI("ws://$hostName/");
            val context = getContext();
            log(hostName!!);
            integrator.start(context, uri);
        }.start()

        Thread {
            while (!isStopped) {
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                notify("qwe $currentDate")
                Thread.sleep(5000);
            }
        }.start();

        return START_NOT_STICKY
    }

    private fun  getContext():Context{
        var cont = this;
        //var cont = MainActivity.context;
        //var cont = createPackageContext("ru.tinkoff.posterminal.singleactivity.MainActivity", 0)
        return cont;
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        log("createNotificationChannel")

        val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }

    private fun notify(msg: String){
        val manager = getSystemService(NotificationManager::class.java);
        log("notify $msg")
        manager.notify(NOTIF_ID, getNotification(msg));
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
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        return notification;
    }
}

