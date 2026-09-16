package com.gotchureviews.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.gotchureviews.app.data.local.InvoiceImageStore
import com.gotchureviews.app.data.model.ExtractionResponse
import com.gotchureviews.app.data.remote.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceRepository @Inject constructor(
    private val apiService: ApiService,
    private val imageStore: InvoiceImageStore,
    @ApplicationContext private val context: Context,
) {
    suspend fun extractInvoice(
        data: ByteArray,
        mimeType: String = "image/jpeg",
    ): ExtractionResponse {
        val filename = if (mimeType == "application/pdf") "invoice.pdf" else "invoice.jpg"
        val requestBody = data.toRequestBody(mimeType.toMediaType())
        val part = MultipartBody.Part.createFormData("image", filename, requestBody)

        val response = apiService.extractInvoice(part)

        // Save image locally for history display
        if (mimeType == "application/pdf") {
            renderPdfFirstPage(data)?.let { jpegData ->
                imageStore.save(jpegData, response.documentHash)
            }
        } else {
            imageStore.save(data, response.documentHash)
        }

        return response
    }

    fun renderPdfFirstPage(pdfData: ByteArray): ByteArray? {
        return try {
            val tempFile = File(context.cacheDir, "temp_render.pdf")
            tempFile.writeBytes(pdfData)
            val fd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fd)
            val page = renderer.openPage(0)

            val scale = 2
            val bitmap = Bitmap.createBitmap(
                page.width * scale,
                page.height * scale,
                Bitmap.Config.ARGB_8888,
            )
            bitmap.eraseColor(android.graphics.Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            renderer.close()
            fd.close()
            tempFile.delete()

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            stream.toByteArray()
        } catch (_: Exception) {
            null
        }
    }
}
