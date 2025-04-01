package com.truvideo.sdk.media.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequestMediaType
import truvideo.sdk.common.exceptions.TruvideoSdkException

internal object FileUriUtil {

    fun getMimeType(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        var mimeType = contentResolver.getType(uri)

        return if (mimeType != null) {
            mimeType
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
            mimeType ?: "unknown/unknown"
        }
    }

    fun getFileUploadRequestMediaType(context: Context, uri: Uri): TruvideoSdkMediaFileUploadRequestMediaType {
        if (isVideo(context, uri)) {
            return TruvideoSdkMediaFileUploadRequestMediaType.VIDEO
        }

        if (isPicture(context, uri)) {
            return TruvideoSdkMediaFileUploadRequestMediaType.IMAGE
        }

        if (isAudio(context, uri)) {
            return TruvideoSdkMediaFileUploadRequestMediaType.AUDIO
        }

        if (isPdf(context, uri)) {
            return TruvideoSdkMediaFileUploadRequestMediaType.DOCUMENT
        }

        return TruvideoSdkMediaFileUploadRequestMediaType.UNKNOWN
    }

    fun isPicture(context: Context, uri: Uri): Boolean {
        val mimeType = getMimeType(context, uri)
        return mimeType.startsWith("image/")
    }

    fun isVideo(context: Context, uri: Uri): Boolean {
        val mimeType = getMimeType(context, uri)
        return mimeType.startsWith("video/")
    }

    fun isAudio(context: Context, uri: Uri): Boolean {
        val mimeType = getMimeType(context, uri)
        return mimeType.startsWith("audio/")
    }

    fun isPdf(context: Context, uri: Uri): Boolean {
        val mimeType = getMimeType(context, uri)
        return mimeType == "application/pdf"
    }

    fun getExtension(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        var extension: String? = null
        if (cursor != null && cursor.moveToFirst()) {
            var columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            if (columnIndex < 0) {
                columnIndex = 0
            }
            val displayName = cursor.getString(columnIndex)
            cursor.close()

            displayName?.let {
                val lastDot = it.lastIndexOf(".")
                if (lastDot != -1) {
                    extension = it.substring(lastDot + 1)
                }
            }
        }
        if (extension != null) {
            return extension as String
        } else {
            val uriSplit = uri.toString().split(".")
            if (uriSplit.isNotEmpty()) {
                return uriSplit.last()
            }
        }

        throw TruvideoSdkException("Invalid file")
    }
}