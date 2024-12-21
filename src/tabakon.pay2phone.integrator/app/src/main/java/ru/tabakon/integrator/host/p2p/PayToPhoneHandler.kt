package ru.tabakon.integrator.host.p2p

import ru.tabakon.integrator.log
import ru.tinkoff.posterminal.p2psdk.PaymentMethod
import ru.tinkoff.posterminal.p2psdk.SoftposResult

data class P2pResult(val identity: String, val isSuccess: Boolean, val msg: String? = null)

class PayToPhoneHandler(private val callBack : (P2pResult) -> Unit) : ru.tinkoff.posterminal.p2psdk.Callback  {
    override fun onError(e: Throwable) {
        log("onError$e")
        callBack.invoke(P2pResult("", false, e.message ?: e.toString()))
    }

    override fun onTransactionRegistered(softposResult: SoftposResult): Boolean {
        var identity = ""
        //var msg = Klaxon().toJsonString(softposResult)
        if(softposResult.paymentMethod == PaymentMethod.QR){
            identity = softposResult.transactionId.toString()
        }
        if(softposResult.paymentMethod == PaymentMethod.NFC){
            identity = softposResult.transactionId.toString()
        }
        callBack.invoke(P2pResult(identity, true))
        return true;
    }
}