package ru.tabakon.integrator.host.integrator

import com.beust.klaxon.Klaxon
import ru.tabakon.integrator.contracts.CreatePaymentOrderCommandMessage
import ru.tabakon.integrator.contracts.IMessage
import ru.tabakon.integrator.contracts.OrderStatusChanged
import ru.tabakon.integrator.contracts.OrderStatusChangedMessage
import ru.tabakon.integrator.contracts.OrderStatusEnum
import ru.tabakon.integrator.contracts.RefundCommandMessage
import ru.tabakon.integrator.contracts.WebSocketMessage
import ru.tabakon.integrator.host.http.IHttpClient

class TabakonClient(private val httpClient: IHttpClient): ITabakonClient {

    override suspend fun dequeueOrder(): IMessage {
        val resp = httpClient.get("Hub/DequeueCommand");
        val message = resp.body;
        val webSocketMessage = Klaxon().parse<WebSocketMessage>(message)
        if(webSocketMessage?.messageType == "CreatePaymentOrderCommand"){
            val createPaymentOrderCommandMessage = Klaxon().parse<CreatePaymentOrderCommandMessage>(message)
            return createPaymentOrderCommandMessage!!;
        }

        if(webSocketMessage?.messageType == "RefundCommand"){
            val createPaymentOrderCommandMessage = Klaxon().parse<RefundCommandMessage>(message)
            return createPaymentOrderCommandMessage!!;
        }

        throw Exception("Error parse dequeueOrder, [$message]")
    }
    override suspend fun changeOrderStatus(orderId: String, orderStatus: OrderStatusEnum, msg: String?) {

        val message = Klaxon().toJsonString(
            OrderStatusChangedMessage(OrderStatusChanged(orderId, orderStatus, msg))
        )
        httpClient.post("Hub/OrderStatusChange", message)
    }
}