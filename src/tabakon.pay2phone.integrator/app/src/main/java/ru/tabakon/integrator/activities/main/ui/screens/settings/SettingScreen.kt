package ru.tabakon.integrator.activities.main.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import ru.tabakon.integrator.IntegrationWorker
import ru.tabakon.integrator.LISTENER_STARTED
import ru.tabakon.integrator.LISTENER_STOPPED
import ru.tabakon.integrator.MainApplication
import ru.tabakon.integrator.activities.common.ui.theme.TabakonPay2PhoneTheme
import ru.tabakon.integrator.activities.main.MainActivity
import ru.tabakon.integrator.activities.main.ui.SystemBroadcastReceiver
import ru.tabakon.integrator.log

@Composable
fun SettingScreen(viewModel : SettingScreenViewModel){

    val state = remember{ mutableStateOf("offline") }

    val connectEnabled =  remember { mutableStateOf(true) }
    val disconnectEnabled = remember { mutableStateOf(false) }

    SystemBroadcastReceiver(systemAction = LISTENER_STOPPED) { recivedIntent ->
        val action = recivedIntent?.action ?: return@SystemBroadcastReceiver

        if(action == LISTENER_STOPPED){
            if(state.value != "offline") {
                state.value = "offline"

                connectEnabled.value = true
            }
        }
    }

    SystemBroadcastReceiver(systemAction = LISTENER_STARTED) { receivedIntent ->
        val action = receivedIntent?.action ?: return@SystemBroadcastReceiver

        if(action == LISTENER_STARTED){
            if(state.value != "online") {
                state.value = "online"

                disconnectEnabled.value = true
            }
        }
    }

    TabakonPay2PhoneTheme(){
        Card(
            modifier = Modifier
                .padding(10.dp)
                .background(color = Color.Transparent)
        ) {
            Text(
                text = "Табакон. Интегратор. PayToPhone.",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = state.value
            )
            Row(
                modifier = Modifier
                    .background(color = Color.Transparent)
            ) {
                TextField(
                    value = viewModel.host,
                    singleLine = true,
                    label = { Text("Хост") },
                    modifier = Modifier
                        .weight(5f)
                        .padding(10.dp),
                    onValueChange = { viewModel.setValueHost(it) }
                )

                TextField(
                    value = viewModel.port.toString(),
                    singleLine = true,
                    label = { Text("Порт") },
                    modifier = Modifier
                        //.fillMaxHeight()
                        .weight(3f)
                        .padding(10.dp),
                    onValueChange = { viewModel.setValuePort(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

                )
            }
            Row {
                Button(
                    onClick = {
                        connectEnabled.value = false

                        log( "onClick Подключиться")

                        IntegrationWorker.startService(MainApplication.applicationContext(), "${viewModel.host}:${viewModel.port}")
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
        }
    }
}