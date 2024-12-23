package ru.tabakon.integrator.host.p2p

import ru.tabakon.integrator.log
import ru.tinkoff.posterminal.p2psdk.PaymentMethod
import ru.tinkoff.posterminal.p2psdk.SoftposResult

data class P2pResult(
    val transactionId: Long,
    val mid: Long,
    val paymentMethod: PaymentMethod,
    val isSuccess: Boolean,
    val msg: String? = null)

class PayToPhoneHandler(private val callBack : (P2pResult) -> Unit) : ru.tinkoff.posterminal.p2psdk.Callback  {
    override fun onError(e: Throwable) {
        log("onError$e")
        val result = P2pResult(
            0,
            0,
            PaymentMethod.UNDEFINED,
            false,
            e.message ?: e.toString()
        )
        callBack.invoke(result)
    }

    override fun onTransactionRegistered(softposResult: SoftposResult): Boolean {
        val result = P2pResult(
            softposResult.transactionId,
            softposResult.mid,
            softposResult.paymentMethod,
            true
        )

        callBack.invoke(result)
        return true;
    }
}