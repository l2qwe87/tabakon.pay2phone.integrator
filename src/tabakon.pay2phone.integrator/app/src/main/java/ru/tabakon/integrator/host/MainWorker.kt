package ru.tabakon.integrator.host

import android.content.Context
import android.content.Intent
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tabakon.integrator.contracts.CreatePaymentOrderCommandMessage
import ru.tabakon.integrator.contracts.OrderStatusEnum
import ru.tabakon.integrator.contracts.PaymentMethodEnum
import ru.tabakon.integrator.contracts.RefundCommandMessage
import ru.tabakon.integrator.host.http.HttpClient
import ru.tabakon.integrator.host.integrator.TabakonClient
import ru.tabakon.integrator.host.p2p.PayToPhoneClient
import java.net.URI

class MainWorker(
    private val context: Context,
    private val uri: URI
) {

    private var isStopped = false;
    private var mainLoopThread : Thread? = null
    private lateinit var tabakonClient : TabakonClient;
    private var tickInProgress = false;

    private val payToPhoneClient = PayToPhoneClient(context)

    fun stop(){
        isStopped = true;
        mainLoopThread = null;
        sendWorkerStatus("Offline")
    }

    fun start(){
        if(mainLoopThread != null){
            throw Exception("Main loop not stopped.");
        }

        val httpClient = HttpClient(uri)
        tabakonClient = TabakonClient(httpClient)

        isStopped = false;
        mainLoopThread = startMainLoop();
    }

    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)
    private fun startMainLoop() : Thread {

        val thread = Thread {
            while (!isStopped) {
                myCoroutineScope.launch {
                    try {
                        tick()
                    } catch (e: Exception) {
                        tickCatch(e)
                    }
                }
                Thread.sleep(2000);
            }
        }

        thread.start();
        return thread;
    }

    private suspend fun tick(){
        tickInProgress = true;
        val message = tabakonClient.dequeueOrder()
        if(message != null){
            if(message is CreatePaymentOrderCommandMessage){

                val orderId = message.messageBody.orderId;
                val amount = message.messageBody.amount ?: 0.0f
                val paymentMethod = PaymentMethodEnum.fromString(message.messageBody.paymentMethod ?: "None")

                payToPhoneClient.pay(
                    amount,
                    paymentMethod
                    ) {

                    myCoroutineScope.launch {
                        val msg = (it.msg ?: "") + "transactionId=" + it.transactionId.toString()+", mid="+ it.mid.toString();
                        val orderStatus = if (it.isSuccess) OrderStatusEnum.Successful else OrderStatusEnum.Fail
                        tabakonClient.changeOrderStatus(orderId, orderStatus, msg)
                    }
                }
            }

            if(message is RefundCommandMessage){

                val orderId = message.messageBody.orderId
                val amount = message.messageBody.amount ?: 0.0f
                val transactionId = message.messageBody.transactionId ?: 0L
                val mid = message.messageBody.mid ?: 0L
                val paymentMethod = PaymentMethodEnum.fromString(message.messageBody.paymentMethod ?: "None")

                payToPhoneClient.refund(
                    amount,
                    paymentMethod,
                    transactionId,
                    mid
                ) {

                    myCoroutineScope.launch {
                        val msg = (it.msg ?: Klaxon().toJsonString(it));
                        val orderStatus = if (it.isSuccess) OrderStatusEnum.Successful else OrderStatusEnum.Fail
                        tabakonClient.changeOrderStatus(orderId, orderStatus, msg)
                    }
                }
            }
        }
        sendWorkerStatus(ru.tabakon.integrator.WORKER_STATUS_ONLINE)
        tickInProgress = false;
    }

    private fun tickCatch(e : Exception){
        sendWorkerStatus(e.message)
    }

    private fun sendWorkerStatus(msg : String?){
        val intent = Intent()
        intent.action = ru.tabakon.integrator.WORKER_STATUS
        intent.putExtra("msg", msg)
        context.sendBroadcast(intent)
    }
}