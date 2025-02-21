package com.example.workmanager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class FileCompressionWorkManager(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val stringUri = workerParams.inputData.getString(GET_URI)
            val compressionTresholdInBytes = workerParams.inputData.getLong(
                WORK_TRESHOLD,
                0L
            )
            val uri = Uri.parse(stringUri)
            val bytes = context.contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            } ?: return@withContext Result.failure()

            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            var outputByte: ByteArray
            var quality = 100

            do {
                val outputStream = ByteArrayOutputStream()
                outputStream.use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
                    outputByte = output.toByteArray()
                    quality -= (quality * 0.1).toInt()
                }
            } while (outputByte.size <= compressionTresholdInBytes && quality > 5)

            val file = File(context.cacheDir, "${workerParams.id}.jpg")
            file.writeBytes(outputByte)

            Result.success(
                workDataOf(
                    FILE_PATH to file.absolutePath
                )
            )

        }


    }

    companion object {
        const val WORK_TRESHOLD = "FILE_COMPRESSION_WORKER"
        const val GET_URI = "GET_URI"
        const val FILE_PATH = "FILE_NAME"
    }

}