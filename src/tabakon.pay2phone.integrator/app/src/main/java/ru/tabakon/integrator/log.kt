package ru.tabakon.integrator

import android.content.Context
import android.content.Intent
import android.util.Log

fun log(msg: String) {
    Log.d("TB", msg)
}

fun log(context: Context, msg: String ){

    log(msg);

    val intent = Intent()
    intent.action = ru.tabakon.integrator.INFO
    intent.putExtra("msg", msg)
    context.sendBroadcast(intent)
}