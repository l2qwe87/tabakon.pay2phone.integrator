package ru.tabakon.integrator.host.p2p

import ru.tabakon.integrator.contracts.PaymentMethodEnum

interface IPayToPhoneClient {
    fun pay(amount: Float, method: PaymentMethodEnum, callBack: (P2pResult) -> Unit)
    fun refund(amount: Float, method: PaymentMethodEnum, callBack: (P2pResult) -> Unit)
}
