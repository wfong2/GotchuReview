package com.gotchureviews.app.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceImageStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val directory: File by lazy {
        File(context.filesDir, "InvoiceImages").also { it.mkdirs() }
    }

    private fun fileFor(documentHash: String): File {
        val sanitized = documentHash.replace(":", "_")
        return File(directory, "$sanitized.jpg")
    }

    fun save(imageData: ByteArray, documentHash: String) {
        fileFor(documentHash).writeBytes(imageData)
    }

    fun loadBitmap(documentHash: String): Bitmap? {
        val file = fileFor(documentHash)
        if (!file.exists()) return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    fun thumbnail(documentHash: String, maxSize: Int = 60): Bitmap? {
        val bitmap = loadBitmap(documentHash) ?: return null
        val scale = maxSize.toFloat() / maxOf(bitmap.width, bitmap.height)
        if (scale >= 1f) return bitmap
        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun hasImage(documentHash: String): Boolean = fileFor(documentHash).exists()
}
