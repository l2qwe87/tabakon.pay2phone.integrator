package ru.tabakon.integrator.socket

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


public interface IWebSocketCallback {
    public abstract fun onOpen(handshakedata: ServerHandshake?): kotlin.Unit
    public abstract fun onClose(code: Int, reason: String?, remote: Boolean): kotlin.Unit
    public abstract fun onMessage(message: String?): kotlin.Unit
    public abstract fun onError(ex: Exception?): kotlin.Unit
}
class TabakonWebSocketClient (serverUri: URI, private val webSocketCallback: IWebSocketCallback) : WebSocketClient(serverUri) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        // When WebSocket connection opened
        webSocketCallback.onOpen(handshakedata);
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        // When WebSocket connection closed
        webSocketCallback.onClose(code, reason, remote);
    }

    override fun onMessage(message: String?) {
        // When Receive a message we handle it at MainActivity
        //messageListener.invoke(message ?: "")

        webSocketCallback.onMessage(message);
    }

    override fun onError(ex: Exception?) {
        // When An error occurred
        webSocketCallback.onError(ex);
    }

    fun sendMessage(message: String) {
        send(message)
    }
}