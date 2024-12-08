package ru.tabakon.integrator.p2pimp

import android.content.Context
import android.content.Intent
import com.beust.klaxon.Klaxon
import ru.tabakon.integrator.http.TabakonHttpClientBuilder
import ru.tabakon.integrator.log
import ru.tabakon.integrator.socket.CreatePaymentOrderCommandMessage
import ru.tabakon.integrator.socket.OrderStatusChanged
import ru.tabakon.integrator.socket.OrderStatusChangedMessage
import ru.tabakon.integrator.socket.OrderStatusEnum
import ru.tabakon.integrator.socket.PaymentMethodEnum
import ru.tabakon.integrator.socket.WebSocketMessage
import java.net.URI

class Integrator{
    private var integratorListener: IIntegratorListener? = null
    private var payToPhoneClient: IPayToPhoneClient? = null
    private var latestOrderId: String? = null

    private lateinit var context: Context;

    /*val status: IntegratorListenerStatusEnum
        get() = integratorListener?.status ?: IntegratorListenerStatusEnum.Stopped
     */

    fun start(context: Context, uri: URI){
        this.context = context;
        payToPhoneClient = PayToPhoneClient(context)
        //integratorListener = IntegratorListenerBuilder()
        integratorListener = TabakonHttpClientBuilder()
            .setUri(uri)
            .setOnMessage {
                if(it != null) {
                    log("setOnMessage $it")

                    val intent = Intent()
                    intent.action = ru.tabakon.integrator.LISTENER_MESSAGE_RECEIVED
                    intent.putExtra("msg", it)
                    context.sendBroadcast(intent)

                    handleMessage(it)
                }
            }
            .setOnOpen {
                val intent = Intent()
                intent.action = ru.tabakon.integrator.LISTENER_STARTED
                context.sendBroadcast(intent)
            }
            .setOnClose {
                val intent = Intent()
                intent.action = ru.tabakon.integrator.LISTENER_STOPPED
                context.sendBroadcast(intent)
            }
            .setOnSending {
                if(it != null) {
                    val intent = Intent()
                    intent.action = ru.tabakon.integrator.LISTENER_MESSAGE_SENDING
                    intent.putExtra("msg", it)
                    context.sendBroadcast(intent)
                }
            }
            .build()

        integratorListener!!.start()
    }

    fun stop(){
        if(integratorListener != null) {
            integratorListener!!.stop()
            integratorListener = null
        }
    }

    private fun handleMessage(message: String){
        if(message == null || message.isEmpty()){
            return
        }
        log("handleMessage $message")
        val webSocketMessage = Klaxon().parse<WebSocketMessage>(message)
        if(webSocketMessage?.messageType == "CreatePaymentOrderCommand"){
            handleCreatePaymentOrderCommandMessage(message)
        }

        if(webSocketMessage?.messageType == "RefundCommand"){
            handleRefundCommandMessage(message)
        }
    }

    private fun handleCreatePaymentOrderCommandMessage(message: String){
        log("handleCreatePaymentOrderCommandMessage $message")
        val createPaymentOrderCommandMessage = Klaxon().parse<CreatePaymentOrderCommandMessage>(message)
        if(createPaymentOrderCommandMessage?.messageBody != null) {
            val messageBody = createPaymentOrderCommandMessage.messageBody
            val amount = messageBody.amount ?: 0.0f
            val paymentMethod = PaymentMethodEnum.fromString(messageBody.paymentMethod ?: "None")

            payToPhoneClient!!.pay(amount, paymentMethod) { handlePayToPhoneResult(it) }
            latestOrderId = messageBody.orderId
            sendOrderStatusChanged(OrderStatusEnum.Created)
        }
    }

    private fun handleRefundCommandMessage(message: String){
        log("handleRefundCommandMessage $message")
        /*val createPaymentOrderCommandMessage = Klaxon().parse<RefundCommandMessage>(message)
        if(createPaymentOrderCommandMessage?.messageBody != null) {
            val messageBody = createPaymentOrderCommandMessage.messageBody
            val amount = messageBody.amount ?: 0.0f
            val paymentMethod = PaymentMethodEnum.fromString(messageBody.paymentMethod ?: "None")

            payToPhoneClient!!.refund(amount, paymentMethod) { handlePayToPhoneResult(it) }
            latestOrderId = messageBody.orderId
            sendOrderStatusChanged(OrderStatusEnum.Created)
        }*/
    }


    fun handlePayToPhoneResult(result: P2pResult){
        val msg = ( result.msg ?: "" ) + "\n" + result.identity;
        val orderStatus = if (result.isSuccess) OrderStatusEnum.Successful else OrderStatusEnum.Fail
        sendOrderStatusChanged(orderStatus, result.msg)

        //val intent = Intent(context, ResultActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //context.startActivity(intent);
    }

    fun sendOrderStatusChanged(orderStatus: OrderStatusEnum, msg: String? = null){
        Thread {
            val message = Klaxon().toJsonString(
                OrderStatusChangedMessage(OrderStatusChanged(latestOrderId, orderStatus, msg))
            )
            if (integratorListener != null)
                integratorListener!!.sendMessage(message)
        }.start()

//        if (latestOrderId != "1111111" && (orderStatus == OrderStatusEnum.Successful || orderStatus == OrderStatusEnum.Fail)) {
//            val intent = Intent(context, ResultActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(intent);
//        }
    }
}