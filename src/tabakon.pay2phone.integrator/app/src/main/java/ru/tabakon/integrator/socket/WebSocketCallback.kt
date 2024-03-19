package ru.tabakon.integrator.socket

import com.beust.klaxon.Klaxon
import org.java_websocket.handshake.ServerHandshake
import ru.tabakon.integrator.IntegrationWorker
import ru.tabakon.integrator.MainActivity
import ru.tabakon.integrator.log

public class WebSocketCallback(private val _host: IntegrationWorker): IWebSocketCallback{
    override fun onOpen(handshakedata: ServerHandshake?) {
        log("WebSocket opened");
        _host.socketOpened();
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        log("WebSocket closed");
        _host.socketClosed();
    }

    override fun onMessage(message: String?) {
        log("WebSocket message:"+message)

        if(message != null) {
            val webSocketMessage = Klaxon().parse<WebSocketMessage>(message);
            if(webSocketMessage?.MessageType == "CreatePaymentOrderCommand"){
                val createPaymentOrderCommandMessage = Klaxon().parse<CreatePaymentOrderCommandMessage>(message);
                if(createPaymentOrderCommandMessage?.MessageBody != null) {
                    _host.CreatePaymentOrderCommand(createPaymentOrderCommandMessage.MessageBody);
                }
            }

        }
    }

    override fun onError(ex: Exception?) {
        log("onError:"+ex);
    }

}