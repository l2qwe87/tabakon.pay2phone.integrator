package ru.tabakon.integrator.socket

import org.java_websocket.handshake.ServerHandshake

interface IWebSocketCallback {
    fun onOpen(handshake: ServerHandshake?)
    fun onClose(code: Int, reason: String?, remote: Boolean)
    fun onMessage(message: String?)
    fun onError(ex: Exception?)
}