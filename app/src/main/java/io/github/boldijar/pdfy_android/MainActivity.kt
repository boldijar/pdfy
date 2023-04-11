package io.github.boldijar.pdfy_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.github.boldijar.pdfy.PdfyParser
import io.github.boldijar.pdfy.PdfyType
import io.github.boldijar.pdfy.ui.PdfyImageLoader
import io.github.boldijar.pdfy.ui.PdfyView

class MainActivity : AppCompatActivity() {

    class GlideLoader : PdfyImageLoader() {
        override fun loadImage(path: String, imageView: ImageView) {
            Glide.with(imageView).load(path).into(imageView)
        }
    }

    lateinit var pdfyView: PdfyView
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val option1 = menu?.add(Menu.NONE, 1, Menu.NONE, "Load PDF from Assets")
        val option2 = menu?.add(Menu.NONE, 2, Menu.NONE, "Load PDF from Internet")
        option1?.setOnMenuItemClickListener {
            pdfyView.setPdf(
                path = "GHIDUL-CLIENTULUI.pdf",
                type = PdfyType.FROM_ASSETS
            )
            true
        }
        option2?.setOnMenuItemClickListener {
            pdfyView.setPdf(
                path = "http://www.ignaciouriarte.com/works/18/pdfs/A100page47.pdf",
                type = PdfyType.FROM_INTERNET
            )
            true
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PdfyParser.init(GlideLoader())
        pdfyView = findViewById(R.id.pdfy_view)
    }
}