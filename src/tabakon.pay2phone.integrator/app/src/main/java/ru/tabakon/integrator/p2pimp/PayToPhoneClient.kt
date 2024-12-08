package ru.tabakon.integrator.p2pimp

import android.content.Context
import ru.tabakon.integrator.log
import ru.tabakon.integrator.socket.PaymentMethodEnum
import ru.tinkoff.posterminal.p2psdk.PaymentTransactionData
import ru.tinkoff.posterminal.p2psdk.RefundTransactionData


interface IPayToPhoneClient {
    fun pay(amount: Float, method: PaymentMethodEnum, callBack: (P2pResult) -> Unit)
    fun refund(amount: Float, method: PaymentMethodEnum, callBack: (P2pResult) -> Unit)
}

//data class  ImpTransactionData(override val amount: Long,override val paymentMethod: PaymentMethod) :
//    ru.tinkoff.posterminal.p2psdk.TransactionData;

class PayToPhoneClient(private val context : Context): IPayToPhoneClient {

    private val softposManager = ru.tinkoff.posterminal.p2psdk.TSoftposManager(context);
    //private val softposManager = ru.tabakon.integrator.p2psdk.SoftposManager.INSTANCE

    override fun pay(amount: Float, method: PaymentMethodEnum, callBack : (P2pResult) -> Unit) {
        val payToPhoneHandler = PayToPhoneHandler(callBack)
        val tr = PaymentTransactionData((amount * 100).toLong(), toPaymentMethod(method));

        Thread {
            softposManager.payToPhone(
                tr,
                payToPhoneHandler,
            )
        }.start()
    }

    override fun refund(amount: Float, method: PaymentMethodEnum, callBack : (P2pResult) -> Unit) {
        log("refund")
        val payToPhoneHandler = PayToPhoneHandler(callBack)

        val tr = RefundTransactionData((amount * 100).toLong(), toPaymentMethod(method), 1, 1);

        Thread {
            softposManager.payToPhone(
                tr,
                payToPhoneHandler
            )
        }.start()
    }

    private fun toPaymentMethod(method: PaymentMethodEnum) : ru.tinkoff.posterminal.p2psdk.PaymentMethod{
        var paymentMethod = ru.tinkoff.posterminal.p2psdk.PaymentMethod.UNDEFINED

        if(method == PaymentMethodEnum.NFC){
            paymentMethod = ru.tinkoff.posterminal.p2psdk.PaymentMethod.NFC
        }
        if(method == PaymentMethodEnum.QR){
            paymentMethod = ru.tinkoff.posterminal.p2psdk.PaymentMethod.QR
        }
        return paymentMethod
    }
}