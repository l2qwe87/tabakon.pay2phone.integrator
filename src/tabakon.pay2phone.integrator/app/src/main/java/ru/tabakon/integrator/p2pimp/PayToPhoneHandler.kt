package ru.tabakon.integrator.p2pimp

import ru.tabakon.integrator.log
import ru.tinkoff.posterminal.p2psdk.SoftposResult

data class P2pResult(val identity: String, val isSuccess: Boolean, val msg: String? = null)

class PayToPhoneHandler(private val callBack : (P2pResult) -> Unit) : ru.tinkoff.posterminal.p2psdk.Callback  {
    override fun onError(e: Throwable) {
        log("onError$e")
        callBack.invoke(P2pResult("", false, e.message ?: e.toString()))
    }

    override fun onSuccess(softposResult: SoftposResult) {
        var identity = ""
        if(softposResult is SoftposResult.Qr){
            identity = softposResult.paymentId.toString()
        }
        if(softposResult is SoftposResult.Nfc){
            identity = softposResult.rrn
        }
        callBack.invoke(P2pResult(identity, true))
    }
}