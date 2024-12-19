package ru.tabakon.integrator.activities.main.ui.screens.home

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

@Composable
fun HomeScreen(
    navigateToSettings: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {  },
                    onDoubleTap = { navigateToSettings() },
                    onLongPress = {  },
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