package com.touchetvsdk.player

import android.content.Context
import android.widget.Toast

private var toast: Toast? = null
fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(this, text, duration).apply { show() }
}