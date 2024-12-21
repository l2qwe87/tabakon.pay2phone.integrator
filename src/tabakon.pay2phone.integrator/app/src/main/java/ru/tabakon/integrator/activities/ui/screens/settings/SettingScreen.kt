package ru.tabakon.integrator.activities.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.tabakon.integrator.MainApplication
import ru.tabakon.integrator.WORKER_STATUS
import ru.tabakon.integrator.activities.MainActivity
import ru.tabakon.integrator.activities.common.ui.theme.TabakonPay2PhoneTheme
import ru.tabakon.integrator.activities.ui.SystemBroadcastReceiver
import ru.tabakon.integrator.host.MainService
import ru.tabakon.integrator.log

@Composable
fun SettingScreen(
    viewModel : SettingScreenViewModel,
    navigateToBack: () -> Unit,
    ){

    val state = remember{ mutableStateOf("???") }

    val connectEnabled =  remember { mutableStateOf(true) }
    val disconnectEnabled = remember { mutableStateOf(false) }

    SystemBroadcastReceiver(systemAction = WORKER_STATUS) { receivedIntent ->
        val action = receivedIntent?.action ?: return@SystemBroadcastReceiver

        if(action == WORKER_STATUS){
            val msg = receivedIntent.getStringExtra("msg")
            state.value = msg ?: "???";
        }
    }


    TabakonPay2PhoneTheme(
    ){
        Card(
            border = BorderStroke(2.dp, Color.Black),
            modifier = Modifier
                .padding(10.dp)
                .background(color = Color.Transparent)
        ) {
            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    navigateToBack();
                }
            ) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "back"
                )
            }
            Text(
                text = "Настройка интегратора.",
                modifier = Modifier
                    .padding(10.dp),
                style = MaterialTheme.typography.titleLarge,
            )
            Row(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .padding(10.dp),
            ) {
                TextField(
                    value = viewModel.host,
                    singleLine = true,
                    label = { Text("Хост") },
                    modifier = Modifier
                        .weight(5f),
                    onValueChange = { viewModel.setValueHost(it) }
                )

                TextField(
                    value = viewModel.port.toString(),
                    singleLine = true,
                    label = { Text("Порт") },
                    modifier = Modifier
                        .weight(3f),
                    onValueChange = { viewModel.setValuePort(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

                )
            }
            Row(
                modifier = Modifier
                    .padding(10.dp),
            ) {
                Button(
                    onClick = {
                        connectEnabled.value = false

                        log( "onClick Применить")
                        MainService.stopService(MainActivity.context)

                        MainApplication.settingsStorage().setHost(viewModel.host)
                        MainApplication.settingsStorage().setPort(viewModel.port)

                        MainService.startService(MainApplication.applicationContext(), "${viewModel.host}:${viewModel.port}")
                    },
                    enabled = true,//connectEnabled.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green,
                    ),
                    content = { Text("Применить") }
                )
                Button(
                    onClick = {
                        disconnectEnabled.value = false

                        log( "onClick Стоп")
                        MainService.stopService(MainActivity.context)

                    },
                    enabled = true,//disconnectEnabled.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                    ),
                    content = { Text("Стоп") }
                )
            }
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = state.value
            )
        }
    }
}