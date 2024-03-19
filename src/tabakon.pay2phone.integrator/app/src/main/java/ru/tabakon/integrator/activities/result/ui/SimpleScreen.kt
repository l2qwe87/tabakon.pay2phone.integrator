package ru.tabakon.integrator.activities.result.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SimpleScreen() {
    Column{
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ){
            Text(
                text = "RESULT"
            )
        }

    }
}