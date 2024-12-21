package ru.tabakon.integrator.utilits

import android.content.Context
import android.content.SharedPreferences




class SettingsStorage(private val context: Context) {

    private val APP_RPEF = "tabakonPreferenceName"
    private val PREF_HOST = "host";
    private val PREF_PORT = "port";

    fun getHost():String {
        val sp = context.getSharedPreferences(APP_RPEF, 0);
        val value = sp.getString(PREF_HOST, "");
        return value!!;
    }

    fun setHost(value: String){
        val editor: SharedPreferences.Editor = context.getSharedPreferences(APP_RPEF, 0).edit()
        editor.putString(PREF_HOST, value)
        editor.apply()
    }

    fun getPort():Int {
        val sp = context.getSharedPreferences(APP_RPEF, 0);
        val value = sp.getInt(PREF_PORT, 5148);
        return value
    }

    fun setPort(value: Int){
        val editor: SharedPreferences.Editor = context.getSharedPreferences(APP_RPEF, 0).edit()
        editor.putInt(PREF_PORT, value)
        editor.apply()
    }
}