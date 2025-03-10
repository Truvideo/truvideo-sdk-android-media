package com.truvideo.sdk.media.builder

import android.content.Context
import android.net.Uri
import com.truvideo.sdk.media.engines.TruvideoSdkMediaEngine
import com.truvideo.sdk.media.engines.TruvideoSdkMediaFileUploadEngine
import com.truvideo.sdk.media.interfaces.TruvideoSdkMediaCallback
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequest
import com.truvideo.sdk.media.model.TruvideoSdkMediaFileUploadRequestMediaType
import com.truvideo.sdk.media.model.TruvideoSdkMediaMetadata
import com.truvideo.sdk.media.model.TruvideoSdkMediaTags
import com.truvideo.sdk.media.repository.TruvideoSdkMediaFileUploadRequestRepository
import com.truvideo.sdk.media.usecases.GetMediaDurationUseCase
import com.truvideo.sdk.media.util.FileUriUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import truvideo.sdk.common.exceptions.TruvideoSdkException
import java.io.File
import java.util.UUID

class TruvideoSdkMediaFileUploadRequestBuilder internal constructor(
    private val context: Context,
    val filePath: String,
    private val mediaRepository: TruvideoSdkMediaFileUploadRequestRepository,
    private val getMediaDurationUseCase: GetMediaDurationUseCase,
    private val engine: TruvideoSdkMediaEngine
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var tagsBuilder = TruvideoSdkMediaTags.builder()
    private var metadataBuilder = TruvideoSdkMediaMetadata.builder()
    private var deleteOnComplete = false
    private var includeInReport: Boolean = false
    private var isLibrary: Boolean = false

    // Tags
    fun addTag(key: String, value: String) {
        this.tagsBuilder[key] = value
    }

    fun removeTag(key: String) = this.tagsBuilder.remove(key)

    fun clearTags() = this.tagsBuilder.clear()

    fun setTags(value: TruvideoSdkMediaTags) {
        this.tagsBuilder = TruvideoSdkMediaTags.builder(
            map = value.map
        )
    }

    // Metadata
    fun addMetadata(key: String, value: String) {
        this.metadataBuilder[key] = value
    }

    fun addMetadata(key: String, value: List<String>) {
        this.metadataBuilder[key] = value
    }

    fun addMetadata(key: String, value: Array<String>) {
        this.metadataBuilder[key] = value.toList()
    }

    fun addMetadata(key: String, value: TruvideoSdkMediaMetadata) {
        this.metadataBuilder[key] = value
    }

    fun removeMetadata(key: String) = this.metadataBuilder.remove(key)

    fun clearMetadata() = this.metadataBuilder.clear()

    fun setMetadata(value: TruvideoSdkMediaMetadata) {
        this.metadataBuilder = TruvideoSdkMediaMetadata.builder(
            map = value.map
        )
    }

    fun setIncludeInReport(value: Boolean) {
        this.includeInReport = value
    }

    fun setIsLibrary(value: Boolean) {
        this.isLibrary = value
    }

    fun deleteOnComplete(enabled: Boolean = true) {
        this.deleteOnComplete = enabled
    }

    suspend fun build(): TruvideoSdkMediaFileUploadRequest {
        if (engine !is TruvideoSdkMediaFileUploadEngine) {
            throw TruvideoSdkException("Invalid engine")
        }

        val type: TruvideoSdkMediaFileUploadRequestMediaType = FileUriUtil.getFileUploadRequestMediaType(
            context = context,
            uri = Uri.fromFile(File(filePath))
        )

        if (type == TruvideoSdkMediaFileUploadRequestMediaType.UNKNOWN) {
            throw TruvideoSdkException("Invalid file type")
        }

        val duration = try {
            if (type.withDuration) {
                getMediaDurationUseCase(filePath)
            } else {
                null
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw TruvideoSdkException("Fail to get the media duration")
        }

        val media = TruvideoSdkMediaFileUploadRequest(
            id = UUID.randomUUID().toString(),
            filePath = filePath,
            durationMilliseconds = duration,
            type = type,
            tags = tagsBuilder.build(),
            metadata = metadataBuilder.build(),
            deleteOnComplete = deleteOnComplete,
            includeInReport = includeInReport,
            isLibrary = isLibrary
        )
        media.engine = engine
        mediaRepository.insert(media)
        return media
    }

    fun build(callback: TruvideoSdkMediaCallback<TruvideoSdkMediaFileUploadRequest>) {
        scope.launch {
            val request = build()
            callback.onComplete(request)
        }
    }
}