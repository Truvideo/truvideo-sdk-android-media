@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.truvideo.sdk.media.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.truvideo.sdk.media.data.converters.DateConverter
import com.truvideo.sdk.media.data.converters.MetadataConverter
import com.truvideo.sdk.media.data.converters.TagsConverter
import com.truvideo.sdk.media.data.converters.TruvideoSdkMediaFileUploadRequestMediaTypeConverter
import com.truvideo.sdk.media.data.converters.TruvideoSdkMediaFileUploadStatusConverter
import com.truvideo.sdk.media.engines.TruvideoSdkMediaFileUploadEngine
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaCallback
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaFileUploadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import truvideo.sdk.common.exceptions.TruvideoSdkException
import java.util.Date

@Entity(tableName = "FileUploadRequest")
data class TruvideoSdkMediaFileUploadRequest(
    @PrimaryKey val id: String,
    var filePath: String,
    var durationMilliseconds: Long? = null,
    var errorMessage: String? = null,
    var remoteId: String? = null,
    var remoteUrl: String? = null,
    var uploadProgress: Float? = null,
    var transcriptionUrl: String? = null,
    var transcriptionLength: Float? = null,
    var deleteOnComplete: Boolean = false,
    var includeInReport: Boolean? = null,
    var isLibrary: Boolean? = null,
    @TypeConverters(TruvideoSdkMediaFileUploadRequestMediaTypeConverter::class)
    var type: TruvideoSdkMediaFileUploadRequestMediaType = TruvideoSdkMediaFileUploadRequestMediaType.IMAGE,

    @TypeConverters(TagsConverter::class)
    var tags: TruvideoSdkMediaTags = TruvideoSdkMediaTags(),

    @TypeConverters(MetadataConverter::class)
    var metadata: TruvideoSdkMediaMetadata = TruvideoSdkMediaMetadata(),

    @TypeConverters(TruvideoSdkMediaFileUploadStatusConverter::class)
    var status: TruvideoSdkMediaFileUploadStatus = TruvideoSdkMediaFileUploadStatus.IDLE,

    @TypeConverters(DateConverter::class)
    val createdAt: Date = Date(),

    @TypeConverters(DateConverter::class)
    var updatedAt: Date = Date(),

    internal var externalId: Int? = null,
    internal var bucketName: String = "",
    internal var region: String = "",
    internal var poolId: String = "",
    internal var folder: String = ""
) {

    fun toJson(): String = Json.encodeToString(
        mapOf(
            "id" to id,
            "filePath" to filePath,
            "type" to type.type,
            "errorMessage" to (errorMessage ?: ""),
            "remoteId" to (remoteId ?: ""),
            "remoteUrl" to (remoteUrl ?: ""),
            "uploadProgress" to (uploadProgress?.toString() ?: ""),
            "transcriptionUrl" to (transcriptionUrl ?: ""),
            "transcriptionLength" to (transcriptionLength?.toString() ?: ""),
            "deleteOnComplete" to deleteOnComplete.toString(),
            "includeInReport" to includeInReport.toString(),
            "isLibrary" to if (isLibrary != null) isLibrary.toString() else false.toString(),
            "tags" to tags.map.entries.joinToString(",") { "${it.key}=${it.value}" },
            "metadata" to metadata.map.entries.joinToString(",") { "${it.key}=${it.value}" },
            "status" to status.name,
            "createdAt" to createdAt.time.toString(),
            "updatedAt" to updatedAt.time.toString(),
            "externalId" to (externalId?.toString() ?: "")
        )
    )

    @Ignore
    internal var engine: TruvideoSdkMediaFileUploadEngine? = null

    @Ignore
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun cancel() {
        val e = engine ?: throw TruvideoSdkException("Engine is null")
        e.cancel(id)
    }

    fun cancel(callback: TruvideoSdkMediaCallback<Unit>) {
        scope.launch {
            try {
                cancel()
                callback.onComplete(Unit)
            } catch (exception: Exception) {
                exception.printStackTrace()
                callback.onError(TruvideoSdkException(exception.message ?: "Unknown error"))
            }
        }
    }

    suspend fun pause() {
        val e = engine ?: throw TruvideoSdkException("Engine is null")
        e.pause(id)
    }

    fun pause(callback: TruvideoSdkMediaCallback<Unit>) {
        scope.launch {
            try {
                pause()
                callback.onComplete(Unit)
            } catch (exception: Exception) {
                exception.printStackTrace()
                callback.onError(TruvideoSdkException(exception.message ?: "Unknown error"))
            }
        }
    }

    suspend fun resume() {
        val e = engine ?: throw TruvideoSdkException("Engine is null")
        e.resume(id)
    }

    fun resume(callback: TruvideoSdkMediaCallback<Unit>) {
        scope.launch {
            try {
                resume()
                callback.onComplete(Unit)
            } catch (exception: Exception) {
                exception.printStackTrace()
                callback.onError(TruvideoSdkException(exception.message ?: "Unknown error"))
            }
        }
    }

    suspend fun upload(callback: TruvideoSdkMediaFileUploadCallback) {
        val e = engine ?: throw TruvideoSdkException("Engine is null")
        e.upload(id, callback)
    }

    fun upload(uploadCallback: TruvideoSdkMediaCallback<Unit>, callback: TruvideoSdkMediaFileUploadCallback) {
        scope.launch {
            try {
                upload(callback)
                uploadCallback.onComplete(Unit)
            } catch (exception: Exception) {
                exception.printStackTrace()
                uploadCallback.onError(TruvideoSdkException(exception.message ?: "Unknown error"))
            }
        }
    }

    suspend fun delete() {
        val e = engine ?: throw TruvideoSdkException("Engine is null")
        e.delete(id)
    }

    fun delete(callback: TruvideoSdkMediaCallback<Unit>) {
        scope.launch {
            try {
                delete()
                callback.onComplete(Unit)
            } catch (exception: Exception) {
                exception.printStackTrace()
                callback.onError(TruvideoSdkException(exception.message ?: "Unknown error"))
            }
        }
    }
}