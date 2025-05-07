package com.app.spacenow.ui.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


fun saveImageToAppStorage(context: Context, sourceUri: Uri?): Uri? {
    if (sourceUri == null) return null

    try {
        // Crear un nombre de archivo único basado en timestamp y UUID
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "SPACE_${timeStamp}_${UUID.randomUUID()}.jpg"

        // Crear el archivo en el directorio de archivos de la app
        val storageDir = File(context.filesDir, "space_images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val imageFile = File(storageDir, fileName)

        // Copiar los bytes de la imagen desde la URI de origen
        context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
            FileOutputStream(imageFile).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
            }
        }

        // Generar una URI para el archivo usando FileProvider
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    } catch (e: IOException) {
        Log.e("ImageUtils", "Error al guardar la imagen", e)
        return null
    } catch (e: Exception) {
        Log.e("ImageUtils", "Error inesperado al guardar la imagen", e)
        return null
    }
}