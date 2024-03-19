package ru.tabakon.integrator.activities.result

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tabakon.integrator.activities.common.ui.theme.TabakonPay2PhoneTheme
import ru.tabakon.integrator.activities.result.ui.SimpleScreen

class ResultActivity : ComponentActivity() {
    companion object {
        // This static-like variable is shared among all instances
        lateinit var context: ComponentActivity;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this;

        setContent {
            TabakonPay2PhoneTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column() {
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                        ){
                            SimpleScreen()
                        }
                    }
                }
            }
        }
    }
}