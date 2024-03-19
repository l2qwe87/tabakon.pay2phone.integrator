package ru.tabakon.integrator.socket

enum class PaymentMethodEnum(val value: String) {
    None("None"),
    NFC("NFC"),
    QR("QR");

    companion object {
        fun fromString(value: String) = entries.first { it.value == value }
    }
}

enum class OrderStatusEnum(val value: String) {
    New("New"),
    Created("Created"),
    Successful("Successful"),
    Fail("Fail");
}

interface IMessageBody
interface IMessage

data class WebSocketMessage(val messageType: String)

////////////////////////////////////////////////////////////////////////
//          ResultMessage
////////////////////////////////////////////////////////////////////////
data class OrderStatusChangedMessage(val messageBody: OrderStatusChanged): IMessage{
    val messageType = "OrderStatusChanged"
}
data class OrderStatusChanged(val orderId: String?, val orderStatus: OrderStatusEnum, val description: String? = null): IMessageBody


////////////////////////////////////////////////////////////////////////
//          CreatePaymentOrderCommand
////////////////////////////////////////////////////////////////////////
data class CreatePaymentOrderCommandMessage(val messageBody: CreatePaymentOrderCommand): IMessage

data class CreatePaymentOrderCommand(val orderId: String, val paymentMethod: String?, val amount: Float?): IMessageBody

////////////////////////////////////////////////////////////////////////
//          RefundCommand
////////////////////////////////////////////////////////////////////////
data class RefundCommandMessage(val messageBody: RefundCommand): IMessage

data class RefundCommand(val orderId: String, val paymentMethod: String?, val amount: Float?): IMessageBody
