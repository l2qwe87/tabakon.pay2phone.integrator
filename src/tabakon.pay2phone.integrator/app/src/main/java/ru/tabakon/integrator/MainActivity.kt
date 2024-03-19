package ru.tabakon.integrator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tabakon.integrator.ui.TabakonWebSocketScreen
import ru.tabakon.integrator.ui.TabakonWebSocketViewModel
import ru.tabakon.integrator.ui.theme.TabakonPay2PhoneTheme

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
            TabakonPay2PhoneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column() {
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                        ){
                            TabakonWebSocketScreen(tabakonWebSocketViewModel)
                        }
                    }
                }
            }
        }
    }
}
