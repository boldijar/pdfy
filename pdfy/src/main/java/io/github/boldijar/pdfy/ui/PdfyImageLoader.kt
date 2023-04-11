package io.github.boldijar.pdfy.ui

import android.widget.ImageView
import io.github.boldijar.pdfy.logError

open class PdfyImageLoader {
    open fun loadImage(path: String, imageView: ImageView) {
        logError("Maybe you forgot to add your own PedefeImageLoader? Damn this is the only thing you have to do to make this library work lol. Read the readme here -> https://github.com/boldijar/pdfy")
    }
}