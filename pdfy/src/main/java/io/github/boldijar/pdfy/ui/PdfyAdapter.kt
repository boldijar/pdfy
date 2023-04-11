package io.github.boldijar.pdfy.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.boldijar.pdfy.Pdfy
import io.github.boldijar.pdfy.PdfyParser
import io.github.boldijar.pdfy.R
import io.github.boldijar.pdfy.logError
import java.io.File

class PdfyAdapter(private var pdfy: Pdfy, private val listener: Listener) :
    RecyclerView.Adapter<PdfyAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val page = pdfy.pages[position]
        if (page.loaded) {
            holder.imageView.isVisible = true
            holder.progress.isVisible = false
            val imagePath = page.image
            val loader = PdfyParser.getImageLoader()
            val fileExists = File(imagePath).exists()
            logError("Page $position is loaded! Yey, loading $imagePath, exists? $fileExists")

            loader.loadImage(imagePath, holder.imageView)
        } else {
            logError("Can't load page $position, loading now!")
            holder.imageView.isVisible = false
            holder.progress.isVisible = true
            listener.loadPage(position)
        }
    }

    override fun getItemCount(): Int {
        return pdfy.pageCount
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPdfy(pdfy: Pdfy) {
        this.pdfy = pdfy
        notifyDataSetChanged()
    }

    fun pageUpdated(pageNumber: Int) {
        pdfy = pdfy.copy(
            pages = pdfy.pages.mapIndexed { index, pdfyPage ->
                if (index == pageNumber) {
                    pdfyPage.copy(loaded = true)
                } else {
                    pdfyPage
                }
            }
        )
        notifyItemChanged(pageNumber)
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progress: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
    }

    interface Listener {
        fun loadPage(pageNumber: Int)
    }
}
