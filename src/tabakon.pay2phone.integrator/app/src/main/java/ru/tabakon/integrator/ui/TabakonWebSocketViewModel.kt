package ru.tabakon.integrator.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TabakonWebSocketViewModel {
    var host : String by mutableStateOf("192.168.88.60");
    var port : Int by mutableIntStateOf(5511);

    fun setValueHost(value: String){
        host = value;
    }

    fun setValuePort(value: String){
        port = value.toIntOrNull() ?: 0;
    }
}