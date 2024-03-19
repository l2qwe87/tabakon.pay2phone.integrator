package ru.tabakon.integrator.p2pimp

import org.java_websocket.handshake.ServerHandshake
import ru.tabakon.integrator.log
import ru.tabakon.integrator.socket.IWebSocketCallback
import ru.tabakon.integrator.socket.TabakonWebSocketClient
import java.net.URI

interface IIntegratorListener {
    val status: IntegratorListenerStatusEnum
    fun start()
    fun stop()
    fun sendMessage(message: String)
}

enum class IntegratorListenerStatusEnum{
    Unknown,
    Started,
    Stopped
}


class IntegratorListenerBuilder{
    private lateinit var _uri: URI
    private lateinit var _onMessage: (message: String?) -> Unit
    private lateinit var _onClose: () -> Unit
    private lateinit var _onOpen: () -> Unit
    private lateinit var _onSending: (message: String?) -> Unit

    fun setUri(uri: URI): IntegratorListenerBuilder{
        _uri = uri
        return this
    }

    fun setOnMessage(onMessage: (message: String?) -> Unit): IntegratorListenerBuilder{
        _onMessage = onMessage
        return this
    }

    fun setOnOpen(onOpen: () -> Unit): IntegratorListenerBuilder{
        _onOpen = onOpen
        return this
    }

    fun setOnClose(onClose: () -> Unit): IntegratorListenerBuilder{
        _onClose = onClose
        return this
    }

    fun setOnSending(onSending: (message: String?) -> Unit): IntegratorListenerBuilder{
        _onSending = onSending
        return this
    }

    fun build() : IIntegratorListener{
        return IntegratorListener(_uri, _onMessage, _onOpen, _onClose, _onSending)
    }
}

private class IntegratorListener(
    private val uri: URI,
    private val onMessage: (message: String?) -> Unit,
    private val onOpen: () -> Unit,
    private val onClose: () -> Unit,
    private val onSending: (message: String?) -> Unit
): IIntegratorListener, IWebSocketCallback {

    private var _status: IntegratorListenerStatusEnum = IntegratorListenerStatusEnum.Stopped
    private var _socket : TabakonWebSocketClient? = null


    override val status: IntegratorListenerStatusEnum
        get() = _status

    override fun start() {
        _status = IntegratorListenerStatusEnum.Unknown
        _socket = TabakonWebSocketClient(uri, this)
        _socket?.connect()
    }

    override fun stop() {
        _status = IntegratorListenerStatusEnum.Unknown
        _socket?.close(1001, "user-initiated closing")
        _socket = null
    }

    override fun sendMessage(message: String){
        if(_socket != null){
            _socket?.sendMessage(message)
            onSending.invoke(message)
        }

    }

    /// IWebSocketCallback
    override fun onOpen(handshake: ServerHandshake?) {
        _status = IntegratorListenerStatusEnum.Started
        onOpen.invoke()

    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        _status = IntegratorListenerStatusEnum.Stopped
        onClose.invoke()
    }

    override fun onMessage(message: String?) {
        onMessage.invoke(message)
    }

    override fun onError(ex: Exception?) {

        log(ex?.message ?: ex.toString())
    }

}