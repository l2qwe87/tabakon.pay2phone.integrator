package ru.tabakon.integrator.socket

enum class PaymentMethodEnum(val value: Int) {
    None(0),
    NFC(1),
    QR(2)
}
class WebSocketMessage(val MessageType: String)
class CreatePaymentOrderCommandMessage(val MessageBody: CreatePaymentOrderCommand);


class CreatePaymentOrderCommand(val PaymentOrderId: String, val PaymentMethod: Int?, val PaymentAmount: Float?)