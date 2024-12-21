package ru.tabakon.integrator.activities.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SettingScreenViewModel(host : String, port : Int) {
    var host : String by mutableStateOf(host)//("192.168.88.60");
    var port : Int by mutableIntStateOf(port)//(5148);

    fun setValueHost(value: String){
        host = value;
    }

    fun setValuePort(value: String){
        port = value.toIntOrNull() ?: 0;
    }
}