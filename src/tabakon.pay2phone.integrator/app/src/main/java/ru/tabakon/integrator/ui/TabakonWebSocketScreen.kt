package ru.tabakon.integrator.ui

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tabakon.integrator.IntegrationWorker
import ru.tabakon.integrator.MainActivity
import ru.tabakon.integrator.log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private const val line = "------------------------------------------------------------------------------"

@Composable
fun TabakonWebSocketScreen(tabakonWebSocketViewModel : TabakonWebSocketViewModel) {
    val state = remember{mutableStateOf("offline")}
    val traceLog = remember{mutableStateOf("")}

    val connectEnabled =  remember { mutableStateOf(true) }
    val disconnectEnabled = remember { mutableStateOf(false) }

    fun addTraceLog(msg: String){
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
        val current = LocalDateTime.now().format(formatter)
        traceLog.value = line + "\n" + current + " " + msg + "\n" + traceLog.value
    }

    SystemBroadcastReceiver(systemAction = ru.tabakon.integrator.LISTENER_STOPPED) { recivedIntent ->
        val action = recivedIntent?.action ?: return@SystemBroadcastReceiver

        if(action == ru.tabakon.integrator.LISTENER_STOPPED){
            state.value = "offline"
            addTraceLog("LISTENER_STOPPED")

            connectEnabled.value = true
        }
    }

    SystemBroadcastReceiver(systemAction = ru.tabakon.integrator.LISTENER_STARTED) { receivedIntent ->
        val action = receivedIntent?.action ?: return@SystemBroadcastReceiver

        if(action == ru.tabakon.integrator.LISTENER_STARTED){
            state.value = "online"
            addTraceLog("LISTENER_STARTED")

            disconnectEnabled.value = true
        }
    }

    SystemBroadcastReceiver(systemAction = ru.tabakon.integrator.LISTENER_MESSAGE_RECEIVED) { receivedIntent ->
        val action = receivedIntent?.action ?: return@SystemBroadcastReceiver

        if(action == ru.tabakon.integrator.LISTENER_MESSAGE_RECEIVED){
            val msg = receivedIntent.getStringExtra("msg")
            addTraceLog("LISTENER_MESSAGE_RECEIVED\n$msg")
        }
    }

    SystemBroadcastReceiver(systemAction = ru.tabakon.integrator.LISTENER_MESSAGE_SENDING) { receivedIntent ->
        val action = receivedIntent?.action ?: return@SystemBroadcastReceiver

        if(action == ru.tabakon.integrator.LISTENER_MESSAGE_SENDING){
            val msg = receivedIntent.getStringExtra("msg")
            addTraceLog("LISTENER_MESSAGE_SENDING\n$msg")
        }
    }

    SystemBroadcastReceiver(systemAction = "ru.tinkoff.posterminal.broadcast.RESULT_TRANSACTION") { receivedIntent ->
        val action = receivedIntent?.action ?: return@SystemBroadcastReceiver

        if(action == "ru.tinkoff.posterminal.broadcast.RESULT_TRANSACTION"){
            var msg = ""

            if(receivedIntent.extras != null) {
                val bundle: Bundle = receivedIntent.extras!!
                for (key in bundle.keySet()) {
                    msg =
                        msg + "\n" + key + " : " + (bundle.get(key) ?: "NULL")
                }
            }

            addTraceLog("RESULT_TRANSACTION\n$msg")
        }
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
    ) {
        Text(
            text = "Табакон. Интегратор. PayToPhone.",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = state.value
        )
        Row {
            TextField(
                value = tabakonWebSocketViewModel.host,
                singleLine = true,
                label = { Text("Хост") },
                modifier = Modifier
                    .weight(5f)
                    .padding(10.dp),
                onValueChange = { tabakonWebSocketViewModel.setValueHost(it) }
            )

            TextField(
                value = tabakonWebSocketViewModel.port.toString(),
                singleLine = true,
                label = { Text("Порт") },
                modifier = Modifier
                    //.fillMaxHeight()
                    .weight(3f)
                    .padding(10.dp),
                onValueChange = { tabakonWebSocketViewModel.setValuePort(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

            )
        }
        Row {
            Button(
                onClick = {
                    connectEnabled.value = false

                    log( "onClick Подключиться")
                    IntegrationWorker.startService(MainActivity.context, "${tabakonWebSocketViewModel.host}:${tabakonWebSocketViewModel.port}")
                },
                enabled = connectEnabled.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                ),
                content = { Text("Подключиться") }
            )
            Button(
                onClick = {
                    disconnectEnabled.value = false

                    log( "onClick Отключиться")
                    IntegrationWorker.stopService(MainActivity.context)
                },
                enabled = disconnectEnabled.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                ),
                content = { Text("Отключиться") }
            )
        }
        Row {

            Text(
                text = traceLog.value,
                fontSize = 10.sp,
                lineHeight = 11.sp,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}