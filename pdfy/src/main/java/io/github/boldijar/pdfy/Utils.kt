package io.github.boldijar.pdfy

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import java.io.File

fun Context.getScreenSize(): Pair<Int, Int> {
    val displayMetrics = DisplayMetrics()
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels
    return Pair(screenWidth, screenHeight)
}

fun Context.getPedefeCache(): File {
    val directory = cacheDir.resolve("io.github.boldijar.pdfy_cache")
    if (!directory.exists()) {
        directory.mkdir()
    }
    return directory
}

fun logError(text: String?, error: Throwable? = null) {
    Log.e("PedefeLib", text, error)
}