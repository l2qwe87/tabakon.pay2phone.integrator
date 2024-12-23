package ru.tabakon.integrator.host.integrator

import ru.tabakon.integrator.contracts.IMessage
import ru.tabakon.integrator.contracts.OrderStatusEnum

interface ITabakonClient {
    suspend fun dequeueOrder(): IMessage?
    suspend fun changeOrderStatus(orderId: String, orderStatus: OrderStatusEnum, msg: String? = null)
}