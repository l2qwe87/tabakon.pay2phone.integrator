package ru.tabakon.integrator.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.tabakon.integrator.IntegrationWorker
import ru.tabakon.integrator.MainActivity
import ru.tabakon.integrator.log


@Composable
fun TabakonWebSocketScreen(tabakonWebSocketViewModel : TabakonWebSocketViewModel) {


    val state = remember{mutableStateOf("offline")}

    Column() {
        Text(
            text = "Табакон. Интегратор. PayToPhone.",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = state.value
        )
        Row() {
            TextField(
                value = tabakonWebSocketViewModel.host,
                singleLine = true,
                label = { Text("Хост") },
                modifier = Modifier
                    //.fillMaxHeight()
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
        Row() {
            Button(
                onClick = {
                    state.value = "online"
                    log( "onClick Подключиться")

                    IntegrationWorker.startService(MainActivity.context, "${tabakonWebSocketViewModel.host}:${tabakonWebSocketViewModel.port}")
                },
                content = { Text("Подключиться") }
            )
            Button(
                onClick = {
                    state.value = "offline"
                    log( "onClick Отключиться")
                    IntegrationWorker.stopService(MainActivity.context)
                },
                content = { Text("Отключиться") }
            )
            Button(
                onClick = {
//                    var cont =  createPackageContext("ru.tinkoff.posterminal.PosApp", 0)
//                    log( "onClick Отключиться")
                },
                content = { Text("qwe") }
            )
        }
    }
}