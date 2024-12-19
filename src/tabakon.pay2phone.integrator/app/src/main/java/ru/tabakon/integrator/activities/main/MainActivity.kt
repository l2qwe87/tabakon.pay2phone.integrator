package ru.tabakon.integrator.activities.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import ru.tabakon.integrator.R
import ru.tabakon.integrator.activities.main.ui.navigation.AppNavigation
import ru.tabakon.integrator.activities.main.ui.screens.TabakonWebSocketViewModel

class MainActivity : ComponentActivity() {
    companion object {
        // This static-like variable is shared among all instances
        lateinit var context: MainActivity;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this;

        setContent {
            val tabakonWebSocketViewModel = remember{ TabakonWebSocketViewModel() }

            Image(
                bitmap = ImageBitmap.imageResource(R.drawable.bg_main),
                modifier = Modifier.fillMaxSize(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
            )
            AppNavigation()
        }
    }
}
