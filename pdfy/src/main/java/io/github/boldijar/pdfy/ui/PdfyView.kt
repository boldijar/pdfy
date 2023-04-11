package io.github.boldijar.pdfy.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.github.boldijar.pdfy.Pdfy
import io.github.boldijar.pdfy.PdfyParser
import io.github.boldijar.pdfy.PdfyType
import io.github.boldijar.pdfy.R
import io.github.boldijar.pdfy.ui.PdfyAdapter
import kotlinx.coroutines.*
import java.util.*

class PdfyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), PdfyAdapter.Listener {
    private var progressbar: ProgressBar
    private var recyclerView: RecyclerView
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var pdfyParser: PdfyParser? = null
    private val pdfyAdapter = PdfyAdapter(Pdfy(0, emptyList()), this)

    private fun showProgress(progress: Boolean) {
        progressbar.isVisible = progress
        recyclerView.isVisible = !progress
    }

    fun setPdf(
        path: String,
        type: PdfyType,
        uniqueCacheName: String = UUID.randomUUID().toString()
    ) {
        recyclerView.adapter = null
        showProgress(true)
        uiScope.launch {
            var pdfy: Pdfy
            withContext(Dispatchers.IO) {
                val parser = PdfyParser(
                    path = path,
                    type = type,
                    uniqueCacheName = uniqueCacheName
                )
                pdfyParser = parser
                pdfy =
                    parser.parsePDF(
                        context
                    )
            }
            showProgress(false)
            pdfyAdapter.setPdfy(pdfy)
            recyclerView.adapter = pdfyAdapter
        }
    }

    init {
        View.inflate(context, R.layout.view_pdfy, this)
        progressbar = findViewById(R.id.progress_bar)
        recyclerView = findViewById(R.id.recycler_view)

        PagerSnapHelper().attachToRecyclerView(recyclerView)
        recyclerView.adapter = pdfyAdapter
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        PdfyParser.clearCache(context)
        uiScope.cancel()
    }

    override fun loadPage(pageNumber: Int) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                pdfyParser?.loadPage(context, pageNumber)
            }
            pdfyAdapter.pageUpdated(pageNumber)
        }
    }


}