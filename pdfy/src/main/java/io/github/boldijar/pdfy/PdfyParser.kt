package io.github.boldijar.pdfy

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import io.github.boldijar.pdfy.ui.PdfyImageLoader
import java.io.File
import java.io.FileOutputStream

class PdfyParser(
    val path: String, val type: PdfyType, val uniqueCacheName: String
) {
    companion object {
        private var imageLoader: PdfyImageLoader = PdfyImageLoader()

        fun getImageLoader(): PdfyImageLoader {
            return imageLoader
        }

        fun init(pdfyImageLoader: PdfyImageLoader) {
            imageLoader = pdfyImageLoader
        }

        fun clearCache(context: Context) {
            val file = context.getPedefeCache()
            file.deleteRecursively()
        }
    }

    private fun createPdfFile(context: Context): File {
        return when (type) {
            PdfyType.FROM_ASSETS -> createPdfFileFromAssets(context)
            PdfyType.FROM_INTERNET -> createPdfFileFromInternet(context)
        }
    }

    private fun preparePdfFile(context: Context): File {
        val pdfFolder = context.getPedefeCache().resolve("pdfs")
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir()
        }
        return pdfFolder.resolve(uniqueCacheName.plus(".pdf"))
    }

    private fun createPdfFileFromInternet(context: Context): File {
        val file = preparePdfFile(context)
        if (file.exists()) {
            return file
        }
        PdfDownloader.downloadPdf(path, file)
        return file
    }

    private fun createPdfFileFromAssets(context: Context): File {
        val assetManager = context.assets
        val inputStream = assetManager.open(path)
        val cachedFile = preparePdfFile(context)
        if (cachedFile.exists()) {
            return cachedFile
        }
        val outputStream = FileOutputStream(cachedFile)
        inputStream.copyTo(outputStream)
        outputStream.flush()
        outputStream.close()
        inputStream.close()
        return cachedFile
    }

    fun parsePDF(context: Context): Pdfy {
        try {
            val file = createPdfFile(context)
            val input = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(input)
            val count = renderer.pageCount
            val pdfy = Pdfy(pageCount = count, pages = (0..count).map {
                PdfyPage(
                    image = getPageFile(context, it).absolutePath, loaded = false
                )
            })
            renderer.close()
            return pdfy

        } catch (e: Exception) {
            logError("Error creating images", e)
        }
        return Pdfy(
            pageCount = 0, pages = emptyList()
        )
    }

    private fun savePage(
        pageNumber: Int, renderer: PdfRenderer, size: Pair<Int, Int>, context: Context
    ): String {
        val page = renderer.openPage(pageNumber)
        val bitmap = Bitmap.createBitmap(size.first, size.second, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        return savePageToFile(
            bitmap = bitmap, pageNumber = pageNumber, context = context
        )
    }

    private fun getPageFile(context: Context, pageNumber: Int): File {
        val folderFile = File(context.getPedefeCache(), uniqueCacheName)
        if (!folderFile.exists()) {
            folderFile.mkdir()
        }
        return folderFile.resolve("$pageNumber.jpeg")
    }

    private fun savePageToFile(
        bitmap: Bitmap, pageNumber: Int, context: Context
    ): String {
        val file = getPageFile(context, pageNumber)
        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return file.absolutePath
    }

    fun loadPage(context: Context, pageNumber: Int) {
        try {
            val file = createPdfFile(context)
            val input = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(input)
            val size = context.getScreenSize()
            savePage(pageNumber, renderer, size, context)
        } catch (e: Exception) {
            logError("Error loading page $pageNumber", e)
        }
    }
}