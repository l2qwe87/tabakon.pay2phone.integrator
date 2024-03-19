package ru.tabakon.integrator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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
                        Card(){
                            TabakonWebSocketScreen(tabakonWebSocketViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = name, modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TabakonPay2PhoneTheme {
        Greeting("Android2")
        FilledButton( "button",{})
        Text("Tabakon2")
    }
}

@Composable
fun FilledButton(text: String, onClick: () -> Unit) {
    Button(onClick) {
        Text(text = text, fontSize = 25.sp)
    }
}