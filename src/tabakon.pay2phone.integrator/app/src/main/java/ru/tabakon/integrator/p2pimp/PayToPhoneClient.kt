package ru.tabakon.integrator.p2pimp

import android.content.Context
import ru.tabakon.integrator.log
import ru.tabakon.integrator.socket.PaymentMethodEnum
import ru.tinkoff.posterminal.p2psdk.SoftposInfo


interface IPayToPhoneClient {
    fun pay(amount: Float, method: PaymentMethodEnum, callBack: (P2pResult) -> Unit)
    fun refund(amount: Float, method: PaymentMethodEnum, callBack: (P2pResult) -> Unit)
}

class PayToPhoneClient(private val context : Context): IPayToPhoneClient {

    private val softposManager = ru.tabakon.integrator.p2psdk.SoftposManager.INSTANCE

    override fun pay(amount: Float, method: PaymentMethodEnum, callBack : (P2pResult) -> Unit) {
        val payToPhoneHandler = PayToPhoneHandler(callBack)
        Thread {
            softposManager.payToPhone(
                context,
                (amount * 100).toLong(),
                payToPhoneHandler,
                toPaymentMethod(method)
            )
        }.start()
    }

    override fun refund(amount: Float, method: PaymentMethodEnum, callBack : (P2pResult) -> Unit) {
        log("refund")
        val payToPhoneHandler = PayToPhoneHandler(callBack)
        var softposInfo: SoftposInfo = SoftposInfo.Nfc("1")
        if(method == PaymentMethodEnum.QR){
            softposInfo = SoftposInfo.Qr(1)
        }
        Thread {
            softposManager.refund(
                context,
                (amount * 100).toLong(),
                softposInfo,
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