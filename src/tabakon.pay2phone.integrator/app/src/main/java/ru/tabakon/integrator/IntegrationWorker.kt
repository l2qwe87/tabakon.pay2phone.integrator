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
import ru.tabakon.integrator.socket.CreatePaymentOrderCommand
import ru.tabakon.integrator.socket.PaymentMethodEnum
import ru.tabakon.integrator.socket.TabakonWebSocketClient
import ru.tabakon.integrator.socket.WebSocketCallback
import ru.tinkoff.posterminal.p2psdk.PaymentMethod
import ru.tinkoff.posterminal.p2psdk.SoftposInfo
import ru.tinkoff.posterminal.p2psdk.SoftposResult
import java.net.URI
import java.util.Date

class IntegrationWorker : Service() {
    private val CHANNEL_ID = "TabakonIntegratorService"
    var payToPhoneCallback =  ru.tabakon.integrator.p2psdk.PayToPhoneCallback(this);
    var webSocketCallback = WebSocketCallback(this);
    //var softposManager = ru.tinkoff.posterminal.p2psdk.SoftposManager;
    var softposManager = ru.tabakon.integrator.p2psdk.SoftposManager.INSTANCE;
    lateinit var socket: TabakonWebSocketClient;

    companion object {

        val NOTIF_ID = 1;
        var isStopped = true;
        lateinit var uri: URI;
        fun startService(context: Context, hostName: String) {
            log("startService")
            isStopped = false;
            val startIntent = Intent(context, IntegrationWorker::class.java)
            startIntent.putExtra("inputExtra", hostName)
            ContextCompat.startForegroundService(context, startIntent)

            uri = URI("ws://$hostName/");

        }
        fun stopService(context: Context) {
            log("stopService")
            isStopped = true;

            val stopIntent = Intent(context, IntegrationWorker::class.java)
            context.stopService(stopIntent)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")

        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_MUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service Kotlin Example")
            .setContentText(input)
            //.setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIF_ID, notification)
        //stopSelf();
        Thread {
            socket = TabakonWebSocketClient(uri, webSocketCallback);
            socket.connect();
        }.start()

        Thread {
            while (!isStopped) {
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                notify("qwe $currentDate")

//                //ru.tinkoff.posterminal.singleactivity.MainActivity
//                val sharingIntent: Intent = Intent("ru.tinkoff.posterminal.singleactivity.MainActivity")
//                //sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(Intent.createChooser(sharingIntent, "Open With"))

                Thread.sleep(5000);
            }
            socket.close();
        }.start();

        return START_NOT_STICKY
    }

    var latestResult: SoftposResult? = null;
    fun successSoftposResult(softposResult: SoftposResult){
        latestResult = softposResult;
        log(softposResult.toString());
        var context = getContext();
        softposManager.refund(context, 999, SoftposInfo.Qr(999) , payToPhoneCallback)

    }

    fun socketClosed(){
        //enableWebSocketConnectButton()
        log("socketClosed");
    }

    fun socketOpened(){
        //enableWebSocketDisconnectButton();
        log("socketOpened");
    }

    fun CreatePaymentOrderCommand(createPaymentOrderCommand: CreatePaymentOrderCommand){
        var paymentMethod = PaymentMethod.UNDEFINED;

        if(createPaymentOrderCommand.PaymentMethod == PaymentMethodEnum.NFC.value){
            paymentMethod = PaymentMethod.NFC;
        }
        if(createPaymentOrderCommand.PaymentMethod == PaymentMethodEnum.QR.value){
            paymentMethod = PaymentMethod.QR;
        }
        if(createPaymentOrderCommand.PaymentAmount!=null) {
            run {
                var context = getContext();
                if(createPaymentOrderCommand.PaymentAmount > 0) {
                    softposManager.payToPhone(
                        context,
                        (createPaymentOrderCommand.PaymentAmount * 100).toLong(),
                        payToPhoneCallback,
                        paymentMethod
                    );
                }else{
                    var paymentAmount = createPaymentOrderCommand.PaymentAmount * -1;
                    softposManager.refund(
                        context,
                        (paymentAmount * 100).toLong(),
                        SoftposInfo.Qr(1),
                        payToPhoneCallback
                    );

                }
            }
        }
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

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        //}
    }

    private fun notify(msg: String){
        val manager = getSystemService(NotificationManager::class.java);
        log("notify $msg")
        manager.notify(NOTIF_ID, getNotification(msg));
    }

    private fun getNotification(msg: String) : Notification{
        var notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setContentTitle("Табакон интегратор")
            .setContentText(msg)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        return notification;
    }
}

