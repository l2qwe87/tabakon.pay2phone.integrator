package ru.tabakon.integrator.socket

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class TabakonWebSocketClient (serverUri: URI, private val webSocketCallback: IWebSocketCallback) : WebSocketClient(serverUri) {
    override fun onOpen(handshak: ServerHandshake?) {
        webSocketCallback.onOpen(handshak)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        webSocketCallback.onClose(code, reason, remote)
    }

    override fun onMessage(message: String?) {
        webSocketCallback.onMessage(message)
    }

    override fun onError(ex: Exception?) {
        webSocketCallback.onError(ex)
    }

    fun sendMessage(message: String) {
        send(message)
    }
}