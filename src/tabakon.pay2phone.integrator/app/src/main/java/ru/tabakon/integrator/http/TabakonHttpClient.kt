package ru.tabakon.integrator.http

import ru.tabakon.integrator.p2pimp.IIntegratorListener
import ru.tabakon.integrator.p2pimp.IntegratorListenerStatusEnum
import java.net.URI

class TabakonHttpClientBuilder {
    private lateinit var _uri: URI;
    private lateinit var _onMessage: (message: String?) -> Unit
    private lateinit var _onClose: () -> Unit
    private lateinit var _onOpen: () -> Unit
    private lateinit var _onSending: (message: String?) -> Unit

    fun setUri(uri: URI): TabakonHttpClientBuilder {
        _uri = uri
        return this
    }

    fun setOnMessage(onMessage: (message: String?) -> Unit): TabakonHttpClientBuilder {
        _onMessage = onMessage
        return this
    }

    fun setOnOpen(onOpen: () -> Unit): TabakonHttpClientBuilder {
        _onOpen = onOpen
        return this
    }

    fun setOnClose(onClose: () -> Unit): TabakonHttpClientBuilder {
        _onClose = onClose
        return this
    }

    fun setOnSending(onSending: (message: String?) -> Unit): TabakonHttpClientBuilder {
        _onSending = onSending
        return this
    }

    fun build() : IIntegratorListener{
        val target = TabakonHttpClient(_uri, _onOpen, _onClose, _onSending)
        target.setOnMessage(_onMessage)
        return target
    }
}

class TabakonHttpClient(
    private val _uri: URI,
    private val onOpen: () -> Unit,
    private val onClose: () -> Unit,
    private val onSending: (message: String?) -> Unit
) : IIntegratorListener {



    private lateinit var onMessage: (message: String?) -> Unit
    private val httpClient = SimpleHttpClient(_uri)

    private var isStopped = true
    private var mainLoopThread : Thread? = null
    override val status: IntegratorListenerStatusEnum = IntegratorListenerStatusEnum.Unknown

    override fun start(){
        if(mainLoopThread != null){
            throw Exception("Main loop not stopped.");
        }

        isStopped = false;
        mainLoopThread = startMainLoop();
    }

    override fun stop(){
        isStopped = true
        mainLoopThread = null
        onClose.invoke()
    }

    override fun sendMessage(message: String) {
        httpClient.post("Hub/OrderStatusChange", message){
            onSending(message)
        }
    }

    override fun setOnMessage(onMessage: (message: String?) -> Unit) {
        this.onMessage = onMessage;
    }


    private fun startMainLoop() : Thread {

        val thread = Thread {
            while (!isStopped) {
                try {
                    getOrder();
                } catch(e : Exception) {
                    onClose.invoke()
                }
                Thread.sleep(200);
            }
        }

        thread.start();
        return thread;
    }

    private fun getOrder(){
        httpClient.get("Hub/DequeueCommand") {
            onOpen.invoke()

            if(it.isNotBlank())
                onMessage(it)
        }
    }
}