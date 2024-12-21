package ru.tabakon.integrator.host

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tabakon.integrator.host.http.HttpClient
import ru.tabakon.integrator.host.integrator.TabakonClient
import java.net.URI

class MainWorker(
    private val context: Context,
    private val uri: URI
) {

    private var isStopped = false;
    private var mainLoopThread : Thread? = null
    private lateinit var tabakonClient : TabakonClient;

    fun stop(){
        isStopped = true;
        mainLoopThread = null;
        sendWorkerStatus("Offline")
    }

    fun start(){
        if(mainLoopThread != null){
            throw Exception("Main loop not stopped.");
        }

        val httpClient = HttpClient(uri)
        tabakonClient = TabakonClient(httpClient)

        isStopped = false;
        mainLoopThread = startMainLoop();
    }

    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)
    private fun startMainLoop() : Thread {

        val thread = Thread {
            while (!isStopped) {
                myCoroutineScope.launch {
                    try {
                        tick()
                    } catch (e: Exception) {
                        tickCatch(e)
                    }
                }
                Thread.sleep(200);
            }
        }

        thread.start();
        return thread;
    }

    private suspend fun tick(){
        tabakonClient.dequeueOrder()
        sendWorkerStatus("Online")
    }

    private fun tickCatch(e : Exception){
        sendWorkerStatus(e.message)
    }

    private fun sendWorkerStatus(msg : String?){
        val intent = Intent()
        intent.action = ru.tabakon.integrator.WORKER_STATUS
        intent.putExtra("msg", msg)
        context.sendBroadcast(intent)
    }


}