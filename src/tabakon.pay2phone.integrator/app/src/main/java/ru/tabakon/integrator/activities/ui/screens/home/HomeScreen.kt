package ru.tabakon.integrator.activities.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import ru.tabakon.integrator.R
import ru.tabakon.integrator.WORKER_STATUS
import ru.tabakon.integrator.WORKER_STATUS_ONLINE
import ru.tabakon.integrator.activities.ui.SystemBroadcastReceiver

@Composable
fun HomeScreen(
    navigateToSettings: () -> Unit,
) {

    SystemBroadcastReceiver(systemAction = WORKER_STATUS) { receivedIntent ->
        val action = receivedIntent?.action ?: return@SystemBroadcastReceiver

        if(action == WORKER_STATUS){
            val msg = receivedIntent.getStringExtra("msg")
            if(msg != WORKER_STATUS_ONLINE){
                navigateToSettings()
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { },
                    onDoubleTap = { navigateToSettings() },
                    onLongPress = { },
                    onTap = { }
                )
            },
    ){
        Image(
            bitmap = ImageBitmap.imageResource(R.drawable.bg_main),
            modifier = Modifier.fillMaxSize(),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
    }
}