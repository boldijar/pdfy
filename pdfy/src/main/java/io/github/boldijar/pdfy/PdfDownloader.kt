package io.github.boldijar.pdfy

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class PdfDownloader {

    companion object {
        fun downloadPdf(pdfUrl: String, outputFile: File) {
            val client = OkHttpClient()
            val request = Request.Builder().url(pdfUrl).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream()
                val outputStream = FileOutputStream(outputFile)
                inputStream?.copyTo(outputStream)
                outputStream.flush()
                outputStream.close()
                inputStream?.close()
            } else {
                logError("Failed to download file: ${response.code} ${response.message}")
            }
        }
    }
}