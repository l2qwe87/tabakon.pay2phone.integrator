package ru.tabakon.integrator.p2psdk

import ru.tinkoff.posterminal.p2psdk.SoftposResult

class PayToPhoneCallback(private val _integratorSoftposResultHandler: IIntegratorSoftposResultHandler) : ru.tinkoff.posterminal.p2psdk.Callback {

    override fun onError(e: Throwable) {
        //_host.setText("onError $e");
        _integratorSoftposResultHandler.onError(e);
    }

    override fun onSuccess(softposResult: SoftposResult) {
        //_host.successSoftposResult(softposResult);
        _integratorSoftposResultHandler.onSuccess(softposResult);
    }
}

interface IIntegratorSoftposResultHandler{
    fun onError(e: Throwable) : Unit;
    fun onSuccess(softposResult: SoftposResult) : Unit;
}